package com.bitongchong.bitrpc.consumer.proxy;

import com.bitongchong.bitrpc.protocol.InvokerProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author liuyuehe
 * @date 2020/3/28 9:33
 */
public class RpcProxy {
    @SuppressWarnings("unchecked")
    public static <T> T create(Class<?> clazz) {
        MethodProxy methodProxy = new MethodProxy(clazz);
        Object proxyObject = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, methodProxy);
        return (T) proxyObject;
    }

    private static class MethodProxy implements InvocationHandler {
        private final Class<?> clazz;

        public MethodProxy(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // todo：看下proxy的类型，看下getDeclaringClass执行后的结果
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(proxy, args);
            } else {
                return rpcInvoker(method, args);
            }
        }

        private Object rpcInvoker(Method method, Object[] args) throws InterruptedException {
            InvokerProtocol msg = new InvokerProtocol();
            msg.setClassName(this.clazz.getName());
            msg.setMethodName(method.getName());
            msg.setParams(method.getParameterTypes());
            msg.setValues(args);
            RpcProxyHandler rpcProxyHandler = new RpcProxyHandler();
            EventLoopGroup workGroup = new NioEventLoopGroup();
            Bootstrap client = new Bootstrap();
            client.group(workGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            // 在Netty中，所有的业务逻辑都汇总在了一个队列中
                            // 这个队列包含了各种各样的操作，Netty封装这些处理逻辑成为了一个对象，即无锁化串行任务队列：PipLine
                            ChannelPipeline pipeline = socketChannel.pipeline();

                            // 一些处理逻辑的封装，对编码解码进行解析
                            // 对于自定义协议所生成的内容进行编码、解码
                            // 这两个的作用是还原数据流为：InvokerProtocol对象
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            pipeline.addLast(new LengthFieldPrepender(4));


                            // 实参处理
                            // 这两个是还原出：InvokerProtocol对象中values、params这两个参数
                            pipeline.addLast("encoder", new ObjectEncoder());
                            pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));

                            // 执行属于自己的逻辑
                            // 1、注册，给每一个对象起一个名字，对外提供服务的名字
                            // 2、服务位置需要做一个登记
                            pipeline.addLast(rpcProxyHandler);
                        }
                    });
            ChannelFuture future = client.connect("localhost", 8080).sync();
            try{
                future.channel().writeAndFlush(msg).sync();
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            }
            workGroup.shutdownGracefully();
            return rpcProxyHandler.getResponse();
        }
    }
}


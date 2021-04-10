package com.bitongchong.bitrpc.registry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @author liuyuehe
 * @date 2020/3/27 15:47
 */
public class Registry {
    private int port;

    public Registry(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        // 之前的版本是通过ServerSocket来实现的，但是这儿直接通过Netty来实现，基于NIO，其有Selector主线程和Worker工作线程
        ServerBootstrap server = new ServerBootstrap();
        // 主线程池初始化，对应Selector
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 子线程池初始化，对应用户的具体操作
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        server.group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                // 在Netty中，所有的业务逻辑都汇总在了一个队列中
                // 这个队列包含了各种各样的操作，Netty封装这些处理逻辑成为了一个对象，即无锁化串行任务队列：Pipline
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
                pipeline.addLast(new RegistryHandler());
            }
        })
        .option(ChannelOption.SO_BACKLOG, 128)
        .option(ChannelOption.SO_KEEPALIVE, true);
        // 正式启动服务，相当于一个死循环
        ChannelFuture future = server.bind(port).sync();
        System.out.println("Bit RPC Registry Started!");
        future.channel().closeFuture().sync();
    }

    public static void main(String[] args) throws InterruptedException {
        Registry registry = new Registry(8080);
        registry.start();
    }
}

package com.bitongchong.bitrpc.registry;

import com.bitongchong.bitrpc.protocol.InvokerProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liuyuehe
 * @date 2020/3/27 19:16
 *
 * 1、根据一个包名将所有符合条件的class全部扫描出来，放置到容器中（如果是分布式，就是读取配置文件）
 * 2、给每一个对应的class起一个唯一的名字，做为服务名称保存到一个容器中
 * 3、当客户端有请求过来的时候，就会获取协议内容InvokerProtocol的对象
 * 4、去注册好的容器中找到符合条件的服务
 * 5、通过远程调用Provider，得到返回结果，并回复给客户端
 */
public class RegistryHandler extends ChannelInboundHandlerAdapter {
    private List<String> classNameContext = new ArrayList<>();
    private Map<String, Object> registryMap = new ConcurrentHashMap<>();

    public RegistryHandler() {
        scanClass("com.bitongchong.notebook.note7.provider");
        doRegistry();
    }

    private void doRegistry() {
        if (classNameContext.isEmpty()) {
            return;
        }
        for(String className : classNameContext) {
            try {
                Class<?> clazz = Class.forName(className);
                // todo:查看一下此处是个啥
                Class<?> tag = clazz.getInterfaces()[0];
                String serverName = tag.getName();
                // 此处本来应该存网络路径，从配置文件中读取，在调用的时候进行解析
                // 此处方便，使用反射调用
                registryMap.put(serverName, clazz.newInstance());
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 正常来说应该是读取配置文件进行扫描，此处为演示方便，直接扫描本地
     * @param packageName -
     */
    private void scanClass(String packageName) {
        URL resource = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
        File classPath = new File(resource.getFile());
        for(File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                scanClass(packageName + "." + file.getName());
            } else {
                classNameContext.add(packageName + "." + file.getName().replace(".class", ""));
            }
        }
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            InvokerProtocol protocol = (InvokerProtocol)msg;
            Object result = null;
            if (registryMap.containsKey(protocol.getClassName())) {
                Object service = registryMap.get(protocol.getClassName());
                Method method = service.getClass().getMethod(protocol.getMethodName(), protocol.getParams());
                result = method.invoke(service, protocol.getValues());
            }
            ctx.write(result);
            ctx.flush();
            ctx.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}

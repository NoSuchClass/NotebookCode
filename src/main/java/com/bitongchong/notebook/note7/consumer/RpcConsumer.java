package com.bitongchong.notebook.note7.consumer;

import com.bitongchong.notebook.note7.api.IRpcHelloService;
import com.bitongchong.notebook.note7.api.IRpcService;
import com.bitongchong.notebook.note7.consumer.proxy.RpcProxy;

/**
 * @author liuyuehe
 * @date 2020/3/28 9:28
 */
public class RpcConsumer {
    public static void main(String[] args){
        IRpcHelloService helloService = RpcProxy.create(IRpcHelloService.class);
        System.out.println(helloService.hello("My RPC Consumer!"));

        IRpcService rpcService = RpcProxy.create(IRpcService.class);
        int a = 7, b = 9;
        System.out.println("7 + 9 = " + rpcService.add(a, b));
        System.out.println("7 - 9 = " + rpcService.sub(a, b));
        System.out.println("7 * 9 = " + rpcService.multi(a, b));
        System.out.println("7 / 9 = " + rpcService.div(a, b));
    }
}

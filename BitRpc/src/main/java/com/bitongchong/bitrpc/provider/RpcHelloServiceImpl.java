package com.bitongchong.bitrpc.provider;

import com.bitongchong.bitrpc.api.IRpcHelloService;

/**
 * @author liuyuehe
 * @date 2020/3/27 15:40
 */
public class RpcHelloServiceImpl implements IRpcHelloService {
    @Override
    public String hello(String name) {
        return "hello " + name + " !";
    }
}

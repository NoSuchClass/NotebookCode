package com.bitongchong.notebook.note7.provider;

import com.bitongchong.notebook.note7.api.IRpcHelloService;

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

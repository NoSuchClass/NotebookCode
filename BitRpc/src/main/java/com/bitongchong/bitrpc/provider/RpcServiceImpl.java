package com.bitongchong.bitrpc.provider;

import com.bitongchong.bitrpc.api.IRpcService;

/**
 * @author liuyuehe
 * @date 2020/3/27 15:42
 */
public class RpcServiceImpl implements IRpcService {
    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public int sub(int a, int b) {
        return a - b;
    }

    @Override
    public int multi(int a, int b) {
        return a * b;
    }

    @Override
    public int div(int a, int b) {
        return a / b;
    }
}

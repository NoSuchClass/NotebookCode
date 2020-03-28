package com.bitongchong.notebook.note6.http;

import java.io.IOException;

/**
 * @author liuyuehe
 * @date 2020/3/27 9:59
 */
public abstract class AbstractBitServlet {
    public void service(BitRequest request, BitResponse response) throws IOException {
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            doGet(request, response);
        } else {
            doPost(request, response);
        }
    }

    protected abstract void doPost(BitRequest request, BitResponse response);

    protected abstract void doGet(BitRequest request, BitResponse response) throws IOException;
}

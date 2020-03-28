package com.bitongchong.notebook.note6.servlet;

import com.bitongchong.notebook.note6.http.AbstractBitServlet;
import com.bitongchong.notebook.note6.http.BitRequest;
import com.bitongchong.notebook.note6.http.BitResponse;

import java.io.IOException;

/**
 * @author liuyuehe
 * @date 2020/3/27 13:53
 */
public class SecondServlet extends AbstractBitServlet {
    @Override
    protected void doPost(BitRequest request, BitResponse response) {

    }

    @Override
    protected void doGet(BitRequest request, BitResponse response) throws IOException {
        response.write("这是我的第二个Servlet！");
    }
}

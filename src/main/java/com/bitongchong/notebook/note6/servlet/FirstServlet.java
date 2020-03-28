package com.bitongchong.notebook.note6.servlet;

import com.bitongchong.notebook.note6.http.AbstractBitServlet;
import com.bitongchong.notebook.note6.http.BitRequest;
import com.bitongchong.notebook.note6.http.BitResponse;
import lombok.SneakyThrows;

/**
 * @author liuyuehe
 * @date 2020/3/27 11:36
 */
public class FirstServlet extends AbstractBitServlet {
    @Override
    protected void doPost(BitRequest request, BitResponse response) {

    }

    @SneakyThrows
    @Override
    protected void doGet(BitRequest request, BitResponse response) {
        response.write("This is the Bit Tomcat first instance!");
    }
}

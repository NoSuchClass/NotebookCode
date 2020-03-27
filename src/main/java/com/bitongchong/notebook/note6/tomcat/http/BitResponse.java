package com.bitongchong.notebook.note6.tomcat.http;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author liuyuehe
 * @date 2020/3/27 10:00
 */
public class BitResponse {
    private OutputStream outputStream;

    public BitResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void write(String s) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.1 200 OK\n")
                .append("Content-Type: text/html; charset=utf-8\n")
                .append("\r\n");
        builder.append(s);
        outputStream.write(builder.toString().getBytes());
        outputStream.flush();
        outputStream.close();
    }
}

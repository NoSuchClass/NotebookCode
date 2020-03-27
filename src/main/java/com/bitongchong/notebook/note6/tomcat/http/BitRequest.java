package com.bitongchong.notebook.note6.tomcat.http;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author liuyuehe
 * @date 2020/3/27 10:00
 */
public class BitRequest {
    private String url;
    private String method;

    public BitRequest(InputStream inputStream) throws IOException {
        StringBuilder content = new StringBuilder();
        byte[] buffer = new byte[1024];
        while (inputStream.read(buffer) == buffer.length) {
            content.append(new String(buffer));
        }
        content.append(new String(buffer));
        String requestBody = content.toString();
        String urlLine = requestBody.split("\n")[0];
        this.url = urlLine.split("\\s")[1];
        this.method = urlLine.split("\\s")[0];
        System.out.println(url + "\n" + method);
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }
}

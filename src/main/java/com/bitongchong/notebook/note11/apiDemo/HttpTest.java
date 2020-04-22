package com.bitongchong.notebook.note11.apiDemo;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

/**
 * @author liuyuehe
 * @date 2020/4/14 10:16
 */
public class HttpTest {

    public void sendHttp() throws ClientProtocolException, IOException, IOException {
        HttpClient httpClient = new DefaultHttpClient();
//      HttpGet get = new HttpGet("http://10.167.14.196:8084");
        HttpPost get = new HttpPost("https://echo630.yswebportal.cc");
        httpClient.execute(get);
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        for (int i = 0; i < 100000; i++) {

                    new HttpTest().sendHttp();

        }
    }
}

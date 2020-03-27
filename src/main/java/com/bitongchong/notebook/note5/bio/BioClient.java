package com.bitongchong.notebook.note5.bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

/**
 * @author liuyuehe
 * @date 2020/3/26 10:45
 */
public class BioClient {

    public static void main(String[] args) {
        try {
            Socket client = new Socket("localhost", 8080);
            OutputStream outputStream = client.getOutputStream();
            String info = UUID.randomUUID().toString();
            System.out.println("BIO客户端发送数据：" + info);
            outputStream.write(info.getBytes());
            outputStream.flush();
            outputStream.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

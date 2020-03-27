package com.bitongchong.notebook.note5.bio;


import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * @author liuyuehe
 * @date 2020/3/26 10:45
 */
public class BioServer {
    ServerSocket server;

    public BioServer(int port) {
        try {
            server = new ServerSocket(port);
            System.out.println("BIO服务端已启动，监听端口： " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        BioServer bioServer = new BioServer(8080);
        bioServer.listen();
    }

    public void listen() {
        try {
            while (true) {
                Socket accept = server.accept();
                InputStream inputStream = accept.getInputStream();
                byte[] buffer = new byte[1];
                StringBuilder sb = new StringBuilder();
                int readLine;
                while ((readLine = inputStream.read(buffer)) > 0) {
                    sb.append(new String(buffer, 0, readLine));
                }
                System.out.println("BIO服务端接收数据：" + sb.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

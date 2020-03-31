package com.bitongchong.notebook.note5.aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

public class TimeClient {

    public static void main(String[] args) {
        int port = 7777;
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception e) {
                // 默认端口
            }
        }
        new Thread(new AsyncTimeClientHandler("127.0.0.1", port), "AIO-AsyncTimeClientHandler-001")
                .start();
    }
}

class AsyncTimeClientHandler implements Runnable {

    private AsynchronousSocketChannel client;
    private String host;
    private int port;
    public CountDownLatch latch;

    public AsyncTimeClientHandler(String host, int port) {
        this.port = port;
        this.host = host;
        try {
            client = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        latch = new CountDownLatch(1);
        client.connect(new InetSocketAddress(host, port), this,
                new ConnectCompletionHandler(client));
        try {
            latch.await();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ConnectCompletionHandler implements CompletionHandler<Void, AsyncTimeClientHandler> {

    private AsynchronousSocketChannel client;

    public ConnectCompletionHandler(AsynchronousSocketChannel client) {
        this.client = client;
    }

    @Override
    public void completed(Void result, AsyncTimeClientHandler attachment) {
        byte[] req = "QUERY TIME ORDER".getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
        writeBuffer.put(req);
        writeBuffer.flip();
        client.write(writeBuffer, writeBuffer,
                new ClientWriteCompletionHandler(client, attachment.latch));

    }

    @Override
    public void failed(Throwable exc, AsyncTimeClientHandler attachment) {
        exc.printStackTrace();
        try {
            client.close();
            attachment.latch.countDown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

class ClientWriteCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

    private AsynchronousSocketChannel client;
    private CountDownLatch latch;

    public ClientWriteCompletionHandler(AsynchronousSocketChannel client, CountDownLatch latch) {
        this.client = client;
        this.latch = latch;
    }

    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        if (buffer.hasRemaining()) {
            client.write(buffer, buffer, this);
        } else {
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            client.read(readBuffer, readBuffer, new ClientReadCompletionHandler(client, latch));
        }

    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            client.close();
            latch.countDown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

    private AsynchronousSocketChannel client;
    private CountDownLatch latch;

    public ClientReadCompletionHandler(AsynchronousSocketChannel client, CountDownLatch latch) {
        this.client = client;
        this.latch = latch;
    }

    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        String body;
        try {
            body = new String(bytes, "UTF-8");
            System.out.println("Now is : " + body);
            latch.countDown();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            client.close();
            latch.countDown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

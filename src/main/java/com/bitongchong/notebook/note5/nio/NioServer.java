package com.bitongchong.notebook.note5.nio;

import com.bitongchong.notebook.util.ThreadPoolsUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author liuyuehe
 * @date 2020/3/26 14:17
 */
public class NioServer {

    /**
     * 轮询器
     */
    private Selector selector;
    /**
     * 缓冲区
     */
    private ByteBuffer buffer = ByteBuffer.allocate(1024);
    private boolean isAlive = false;

    public NioServer(int port) {
        try {
            ServerSocketChannel server = ServerSocketChannel.open();
            server.bind(new InetSocketAddress(port));
            // NIO为了兼容BIO是默认采用阻塞模型的
            server.configureBlocking(false);
            selector = Selector.open();
            server.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        try {
            isAlive = true;
            while (isAlive) {
                System.out.println("server is available!");
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> selectionKeyIterator = keys.iterator();
                // 同步体现在此处，每次都只能拿到一个key，处理一种状态
                while (selectionKeyIterator.hasNext()) {
                    SelectionKey key = selectionKeyIterator.next();
                    selectionKeyIterator.remove();
                    process(key);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        isAlive = false;
    }

    /**
     * 这个地方体现了非阻塞，每次请求过来时都会返回一个状态，而不是一直阻塞在一个地方
     * @param key -
     * @throws IOException -
     */
    private void process(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel channel = server.accept();
            channel.configureBlocking(false);
            // 当数据准备就绪时，将key的状态改为可读
            key = channel.register(selector, SelectionKey.OP_READ);
        } else if (key.isReadable()) {
            SocketChannel channel = (SocketChannel) key.channel();
            int readLen = channel.read(buffer);
            if (readLen > 0) {
                buffer.flip();
                String content = new String(buffer.array(), 0, readLen);
                key = channel.register(selector, SelectionKey.OP_WRITE);
                key.attach(content);
                System.out.println("接收到的内容：" + content);
            }
        } else if (key.isWritable()) {
            SocketChannel channel = (SocketChannel) key.channel();
            String content = (String) key.attachment();
            channel.write(ByteBuffer.wrap(("输出内容：" + content).getBytes()));
            channel.close();
        }
    }


    public static void main(String[] args) {
        NioServer server = new NioServer(8080);
        server.listen();
    }

}

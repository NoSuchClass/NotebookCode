package com.bitongchong.notebook.note11.apiDemo;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author liuyuehe
 * @date 2020/4/9 10:45
 */
public class BlockingConsumer {
    static LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public static void main(String[] args) throws InterruptedException {
        new Thread(()->{
            while(true) {
                String poll = null;
                try {
                    poll = queue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("consumer : " + poll);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        for (int i = 0; i < 100; i++) {
            queue.put(i + "");

        }

    }
}

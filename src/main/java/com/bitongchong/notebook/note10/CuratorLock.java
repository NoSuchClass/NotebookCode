package com.bitongchong.notebook.note10;

import com.bitongchong.notebook.util.ThreadPoolsUtil;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author liuyuehe
 * @date 2020/3/30 10:35
 */
public class CuratorLock {
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            ThreadPoolsUtil.getInstance().submitTask(() -> {
                RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
                CuratorFramework client = CuratorFrameworkFactory.builder()
                        .connectString("39.105.83.97:2181")
                        .sessionTimeoutMs(5000)
                        .connectionTimeoutMs(5000)
                        .retryPolicy(retryPolicy)
                        .build();
                client.start();
                System.out.println(Thread.currentThread().getName() + " ---> connected!");
                InterProcessMutex interProcessMutex = new InterProcessMutex(client, "/locks");
                try {
                    System.out.println(Thread.currentThread().getName() + " ---> try to get lock!");
                    interProcessMutex.acquire();
                    System.out.println(Thread.currentThread().getName() + " ---> get lock success!");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        System.out.println("release lock!");
                        interProcessMutex.release();
                        System.out.println(Thread.currentThread().getName() + " --->  release lock success!");
                        client.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }
}

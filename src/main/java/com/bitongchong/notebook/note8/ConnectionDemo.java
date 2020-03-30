package com.bitongchong.notebook.note8;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

/**
 * @author liuyuehe
 * @date 2020/3/28 22:38
 */
public class ConnectionDemo {
    private static void func1() {
        try {
            ZooKeeper zooKeeper = new ZooKeeper("39.105.83.97:2181", 4000, null);
            // CONNECTING :这个状态下进行操作会报错，因为连接还未建立成功，因此有了func2()这个方法
            System.out.println(zooKeeper.getState());
            Thread.sleep(15000);
            // CONNECTED
            System.out.println(zooKeeper.getState());
            zooKeeper.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void func2() {
        try {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            ZooKeeper zooKeeper = new ZooKeeper("39.105.83.97:2181", 4000, watchedEvent -> {
                // 如果收到服务端的链接成功的响应时间
                if (Watcher.Event.KeeperState.SyncConnected == watchedEvent.getState()) {
                    countDownLatch.countDown();
                }
            });
            // CONNECTING
            System.out.println(zooKeeper.getState());
            countDownLatch.await();
            // CONNECTED
            System.out.println(zooKeeper.getState());

            // 添加一个节点，分别是 路径名， 节点值， 节点访问权限， 节点的创建模式（临时还是持久化的节点）
            zooKeeper.create("/zk-server-test", "value".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            Thread.sleep(1000);

            // 获取当前节点的值
            // 此处传递Stat这个对象，能够在创建成功之后更新其version，方便后续操作的版本获取
            Stat stat = new Stat();
            byte[] data = zooKeeper.getData("/zk-server-test", null, stat);
            System.out.println(new String(data));

            zooKeeper.setData("/zk-server-test", "changed-value".getBytes(), stat.getVersion());

            // 重新获取当前节点的值
            byte[] data2 = zooKeeper.getData("/zk-server-test", watchedEvent -> {
                System.out.println("服务器响应后回调显示！");
                System.out.println("当前服务器状态： " + watchedEvent.getState().getIntValue());
            }, stat);
            System.out.println(new String(data2));

            // 删除当前节点
            zooKeeper.delete("/zk-server-test", stat.getVersion());

            zooKeeper.close();
        } catch (IOException | InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        func2();
    }
}

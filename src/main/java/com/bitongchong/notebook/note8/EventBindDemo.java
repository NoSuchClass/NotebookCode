package com.bitongchong.notebook.note8;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author liuyuehe
 * @date 2020/3/28 23:46
 */
public class EventBindDemo implements Watcher {
    static ZooKeeper zooKeeper;

    static {
        try {
            zooKeeper = new ZooKeeper("39.105.83.97:2181", 4000, new EventBindDemo());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static CountDownLatch countDownLatch = new CountDownLatch(1);

    public EventBindDemo() throws IOException {
    }

    public static void main(String[] args) {
        try {

            System.out.println(zooKeeper.getState());
            countDownLatch.await();
            System.out.println(zooKeeper.getState());

            zooKeeper.create("/zk-server-test", "value".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            Thread.sleep(1000);

            Stat stat = new Stat();

            zooKeeper.exists("/zk-server-test", watchedEvent -> {
                System.out.println("------------");
                System.out.println("通过exists进行的事件绑定！");
                System.out.println(watchedEvent.getType() + " -> " + watchedEvent.getPath());
                System.out.println("------------");
                try {
                    // 当数据发生变化的时候， zookeeper 会产生一个 watcher 事件，并且会发送到客户端。但是客户端只会收到一次通知。
                    // 如果后续这个节点再次发生变化，那么之前设置 watcher 的客户端不会再次收到消息。（watcher 是一次性的操作）。
                    // 可以通过循环监听去达永久监听效果
                    zooKeeper.exists("/zk-server-test", true);
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
            });

            Stat stat1 = zooKeeper.setData("/zk-server-test", "changed-value".getBytes(), stat.getVersion());
            Stat stat2 = zooKeeper.setData("/zk-server-test", "changed-value2".getBytes(), stat1.getVersion());
            // 删除当前节点
            // zooKeeper.delete("/zk-server-test", stat2.getVersion());
            // 阻塞查看监听效果
            new CountDownLatch(1).await();
            zooKeeper.close();
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent event) {

        System.out.println("------------");
        System.out.println("全局：通过exists进行的事件绑定！");
        System.out.println(event.getType() + " -> " + event.getPath());
        System.out.println("------------");
        if (Watcher.Event.KeeperState.SyncConnected == event.getState()) {
            countDownLatch.countDown();
        }
        // 这儿是达到了循环监听的效果
        if (Watcher.Event.EventType.NodeDataChanged == event.getType()) {
            try {
                zooKeeper.exists("/zk-server-test", true);
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


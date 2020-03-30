package com.bitongchong.notebook.note10;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author liuyuehe
 * @date 2020/3/29 15:04
 */
public class DistributedLock implements Lock, Watcher {
    private ZooKeeper zk = null;
    /**
     * 定义根节点
     */
    private String rootLock = "/locks";
    /**
     * 等待中的前一个锁
     */
    private String waitLock;
    /**
     * 表示当前的锁
     */
    private String currentLock;

    private CountDownLatch countDownLatch;

    public DistributedLock() {
        try {
            CountDownLatch startLatch = new CountDownLatch(1);
            // 这儿是this的原因：这个类实现了Watcher接口，通过process方法进行回调处理
            zk = new ZooKeeper("39.105.83.97:2181", 40000, watchedEvent -> {
                // 如果收到服务端的链接成功的响应时间
                if (Watcher.Event.KeeperState.SyncConnected == watchedEvent.getState()) {
                    startLatch.countDown();
                }
            });
            startLatch.await();
            // 这儿是false的原因：不需要再额外注册watcher
            Stat stat = zk.exists(rootLock, false);
            if (stat == null) {
                zk.create(rootLock, "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (IOException | InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean tryLock() {
        try {
            // 这儿是创建临时有序节点
            currentLock = zk.create(rootLock + "/", "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println(Thread.currentThread() + "->" + currentLock + "， 尝试竞争锁");
            // 获取所有比当前锁节点更小的节点列表
            List<String> children = zk.getChildren(rootLock, false);
            // 对这些列表进行排序
            SortedSet<String> sortedSet = new TreeSet<>();
            children.forEach(e -> sortedSet.add(rootLock + "/" + e));
            // 获取节点列表中最小的节点
            String firstNode = sortedSet.first();
            // headSet是返回比传入参数更小的参数列表，就是currentLock之前（不包括其本身）的元素
            SortedSet<String> lessThanCurrentSet = sortedSet.headSet(currentLock);
            // 如果当前锁节点是最小节点，那么就代表获取到了分布式锁
            if (currentLock.equals(firstNode)) {
                return true;
            }
            // 如果当前节点不是最小节点，那么就获取lessSet中最后一个元素（即它前面一个比它小的节点），做为等待获取的锁
            if (!lessThanCurrentSet.isEmpty()) {
                waitLock = lessThanCurrentSet.last();
            }
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void lock() {
        if (tryLock()) {
            System.out.println(Thread.currentThread().getName() + "->" + currentLock + "-> 获取锁成功！");
            return;
        }
        try {
            waitForLock(waitLock);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void waitForLock(String prev) throws KeeperException, InterruptedException {
        Stat stat = zk.exists(prev, this);
        if (stat != null) {
            System.out.println(Thread.currentThread().getName() + "->等待锁" + prev + "释放");
            countDownLatch = new CountDownLatch(1);
            countDownLatch.await();
            System.out.println(Thread.currentThread().getName() + "->获取锁成功");
        }
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public void unlock() {
        System.out.println(Thread.currentThread().getName() + "->释放锁");
        try {
            zk.delete(currentLock, -1);
            currentLock = null;
            zk.close();
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Condition newCondition() {
        return null;
    }

    @Override
    public void process(WatchedEvent event) {
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }
}

package com.bitongchong.notebook.note9;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * @author liuyuehe
 * @date 2020/3/29 11:06
 *
 * Guava is to Java that Curator to Zookeeper --- Patrixck Hunt（Zookeeper）
 */
public class CuratorDemo {
    public static void main(String[] args) throws Exception {
        // RetryPolicy为重试策略，第一个参数为baseSleepTimeMs初始的sleep时间，用于计算之后的每次重试的sleep时间
        // 第二个参数为maxRetries，最大重试次数。
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("39.105.83.97:2181")
                // 会话超时时间
                .sessionTimeoutMs(5000)
                // 连接超时时间
                .connectionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                // 包含隔离名称
                // .namespace("base")
                .build();
        client.start();

        // 递归创建所需父节点
        client.create().creatingParentContainersIfNeeded()
                // 创建类型为持久节点
                .withMode(CreateMode.PERSISTENT)
                // 目录及内容
                .forPath("/parentNode/childNode", "init".getBytes());

        byte[] bytes = client.getData().forPath("/parentNode/childNode");
        System.out.println(new String(bytes));

        Stat stat = new Stat();
        client.getData()
                .storingStatIn(stat)
                .forPath("/parentNode/childNode");

        Stat stat1 = client.setData()
                // 指定版本修改
                .withVersion(stat.getVersion())
                .forPath("/parentNode/childNode", "data".getBytes());

        byte[] bytes2 = client.getData().forPath("/parentNode/childNode");
        System.out.println(new String(bytes2));

        client.delete()
                .guaranteed()
                // 递归删除子节点
                .deletingChildrenIfNeeded()
                .withVersion(stat1.getVersion())
                .forPath("/parentNode/childNode");
    }
}

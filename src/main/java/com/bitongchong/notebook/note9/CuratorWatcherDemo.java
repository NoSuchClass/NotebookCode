package com.bitongchong.notebook.note9;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author liuyuehe
 * @date 2020/3/29 11:27
 */
public class CuratorWatcherDemo {
    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("39.105.83.97:2181")
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .build();
        client.start();
        // PathChildrenCache: 监听path节点的子节点的 创建、更新、删除 事件
        // addListenerWithPathChildCache(client, "/bit");
        // NodeCache: 监听path节点的 创建、更新、删除 事件
        // addListenerWithNodeCache(client, "/bit2");
        // TreeCache: 上述两种功能的总和
        addListenerWithTreeCache(client, "/bit3");
        System.in.read();
    }

    private static void addListenerWithPathChildCache(CuratorFramework client, String path) throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, path, true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            System.out.println("获得操作事件：" + pathChildrenCacheEvent.getType());
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start(PathChildrenCache.StartMode.NORMAL);
    }

    private static void addListenerWithNodeCache(CuratorFramework client, String path) throws Exception {
        NodeCache nodeCache = new NodeCache(client, path, false);
        NodeCacheListener nodeCacheListener = () -> {
            System.out.println("节点更新或被创建: " + nodeCache.getCurrentData().getStat());
        };
        nodeCache.getListenable().addListener(nodeCacheListener);
        nodeCache.start();
    }

    private static void addListenerWithTreeCache(CuratorFramework client, String path) throws Exception {
        TreeCache treeCache = new TreeCache(client, path);
        TreeCacheListener treeCacheListener = new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                System.out.println("节点事件： " + event.getType());
            }
        };
        treeCache.getListenable().addListener(treeCacheListener);
        treeCache.start();
    }
}

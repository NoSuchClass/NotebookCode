package com.bitongchong.notebook.util;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author liuyuehe
 * @date 2020/3/2 10:33
 */
public class ThreadPoolsUtil {
    private ExecutorService uploadPool;

    private ThreadPoolsUtil() {
        uploadPool = new ThreadPoolExecutor(2, 16, 15, TimeUnit.MINUTES
                , new ArrayBlockingQueue<>(1024), new NamedThreadFactory("UPLOAD"));
    }

    /**
     * 返回单例
     *
     * @return 工具类单例
     */
    public static ThreadPoolsUtil getInstance() {
        return ThreadPoolManagerHolder.instance;
    }

    /**
     * 弱依赖提交task，如果task提交失败，则在本线程中扔出异常，由本线程相机处理
     *
     * @param task task
     */
    public void submitTask(Runnable task) throws RejectedExecutionException {
        uploadPool.submit(task);
    }

    public void shutdown() {
        uploadPool.shutdown();
    }

    private static class ThreadPoolManagerHolder {
        public static ThreadPoolsUtil instance = new ThreadPoolsUtil();
    }

    private static class NamedThreadFactory implements ThreadFactory {
        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        public NamedThreadFactory(String name) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" + POOL_NUMBER.getAndIncrement() + "-" + name + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }
}

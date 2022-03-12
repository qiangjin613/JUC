package com.juc.practice.thread.pool;

import java.util.concurrent.*;

public class ThreadPoolDemo {
    public static void main(String[] args) {
        // 1. 获取当前操作系统 CPU 核数
        int cpuNum = Runtime.getRuntime().availableProcessors();

        // 2. 假设当前业务为 CPU 密集型
        ExecutorService threadPool = new ThreadPoolExecutor(
                0, /* 常驻线程数量 */
                cpuNum + 1, /* 最大线程数量 */
                60L, /* 线程存活时间 */
                TimeUnit.SECONDS, /* 时间单位 */
                new LinkedBlockingQueue<>(5), /* 阻塞队列 */
                Executors.defaultThreadFactory(), /* 线程工厂 */
                new ThreadPoolExecutor.AbortPolicy()); /* 拒绝策略 */
    }
}
/*
maximumPoolSize 参数设置策略：
根据业务，分为 CPU 密集型、IO 密集型。具体如下：
- CPU 密集型一般公式：CPU核数 + 1；
- IO 密集型一般公式：CPU核数 / (1 - 阻塞系数)。阻塞系数范围：[0.8, 0.9]
 */

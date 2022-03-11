package com.juc.practice.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*
手写一个缓存

目标：
    编写所有缓存必须具备的 3 个方法：
        写：原子（写操作的整个过程必须是一个整体，不可被加塞/打断） + 独占（只能一个线程进行操作，互斥）
        读：共享的，不用保证原子性 + 允许多个线程来进行读操作
        清空：
 */

// ----------------------- 先不加锁实现一个缓存 -----------------------

/**
 * 资源类
 * 用于模拟一个分布式缓存，其底层就是 Map
 */
class Cache {

    private volatile Map<String, Object> map = new HashMap<>();

    private void put(String k, Object v) {
        System.out.println(Thread.currentThread().getName() + " 正在写入：" + k);
        map.put(k, v);
        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " 写入完成");
    }

    private void get(String k) {
        System.out.println(Thread.currentThread().getName() + " 正在读取：" + k);
        Object v = map.get(k);
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " 读取完成：" + v);
    }

    public static void main(String[] args) {
        Cache cache = new Cache();

        // 多个线程来写
        for (int i = 0; i < 5; i++) {
            String temp = String.valueOf(i);
            new Thread(() -> {
                cache.put(temp, temp);
            }, "写" + temp).start();
        }

        // 多个线程来读
        for (int i = 0; i < 5; i++) {
            String temp = String.valueOf(i);
            new Thread(() -> {
                cache.get(temp);
            }, "读" + temp).start();
        }
    }
}
/*
通过运行结果，看到，对于 读既不是原子，也不是独占的
 */

// ----------------------- 实现缓存方法的要求 -----------------------

/*
方法一：使用 ReentrantLock、synchronized 并发性不好，这时写操作的要求保证了，但是对于读操作，也只有一个线程进行了
方法二：使用 ReentrantReadWriteLock
 */

class Cache2 {

    private volatile Map<String, Object> map = new HashMap<>();

    private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    private void put(String k, Object v) {
        rwLock.writeLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 正在写入：" + k);
            map.put(k, v);
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " 写入完成");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    private void get(String k) {
        rwLock.readLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + " 正在读取：" + k);
            Object v = map.get(k);
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " 读取完成：" + v);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public static void main(String[] args) {
        Cache2 cache = new Cache2();

        // 多个线程来写
        for (int i = 0; i < 5; i++) {
            String temp = String.valueOf(i);
            new Thread(() -> {
                cache.put(temp, temp);
            }, "写" + temp).start();
        }

        // 多个线程来读
        for (int i = 0; i < 5; i++) {
            String temp = String.valueOf(i);
            new Thread(() -> {
                cache.get(temp);
            }, "读" + temp).start();
        }
    }
}

package com.juc.practice.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 自旋锁，是指尝试获取锁的线程不会立即阻塞，而是采用循环的方式去尝试获取锁，
 * 这样的好处是减少线程上下文切换的消耗，缺点是循环会消耗 CPU
 *
 * 目标：实现一个自旋锁
 */
public class SpinlockDemo {

    /* 原子引用线程 */
    AtomicReference<Thread> atomicReference = new AtomicReference<>();

    public void myLock() {
        Thread thread = Thread.currentThread();
        System.out.println(thread.getName() + " 进入 lock() 方法");
        /* 自旋 + CAS */
        while (!atomicReference.compareAndSet(null, thread));
        System.out.println(Thread.currentThread().getName() + " 获得锁");
    }

    public void myUnlock() {
        Thread thread = Thread.currentThread();
        atomicReference.compareAndSet(thread, null);
        System.out.println(thread.getName() + " 执行 myUnlock() 释放锁");
    }

    public static void main(String[] args) {
        SpinlockDemo spinlockDemo = new SpinlockDemo();

        new Thread(() -> {
            spinlockDemo.myLock();
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            spinlockDemo.myUnlock();
        }, "A").start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            spinlockDemo.myLock();
            spinlockDemo.myUnlock();
        }, "B").start();
    }
}

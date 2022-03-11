package com.juc.practice.lock;

import java.util.concurrent.locks.ReentrantLock;

/*
可重入锁（又称”递归锁“）的示例
    指的是同一线程外层函数获得锁之后，内层递归函数仍然能获取该锁的代码。
    在同一个线程在外层方法获取锁的时候，在进入内层方法会自动获取锁，方法执行完毕后会自行释放锁。
    也即是说，线程可以进入任何一个它已经拥有的锁所同步着的代码块。
 */

/**
 * 对 synchronized 可重入锁的测试
 */
public class ReentrantLockPractice {

    public synchronized void syncF1() {
        System.out.println(Thread.currentThread().getId() + " 进入 syncF1()");
        syncF2();
        System.out.println(Thread.currentThread().getId() + " 退出 syncF1()");
    }

    public synchronized void syncF2() {
        System.out.println("\t" + Thread.currentThread().getId() + " 进入 syncF2()");
    }

    public static void main(String[] args) {
        ReentrantLockPractice r = new ReentrantLockPractice();

        new Thread(() -> {
            r.syncF1();
        }).start();

        new Thread(() -> {
            r.syncF1();
        }).start();
    }
}
/*
可以看到，同一个线程在获取了 f1 的锁后，又获得了 f2 的锁
 */

/**
 * 对 ReentrantLock 可重入锁的测试
 */
class ReentrantLockPractice2 {

    /* 获取一个非公平的可重入锁 */
    ReentrantLock lock = new ReentrantLock();

    public void syncF1() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getId() + " 进入 syncF1()");
            syncF2();
            System.out.println(Thread.currentThread().getId() + " 退出 syncF1()");
        } finally {
            lock.unlock();
        }
    }

    public void syncF2() {
        lock.lock();
        try {
            System.out.println("\t" + Thread.currentThread().getId() + " 进入 syncF2()");
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ReentrantLockPractice2 r = new ReentrantLockPractice2();

        new Thread(() -> {
            r.syncF1();
        }).start();

        new Thread(() -> {
            r.syncF1();
        }).start();
    }
}

/**
 * 思维扩展：对于 lock.lock(); lock.lock(); 的测试
 */
class ReentrantLockPractice3 {

    /* 获取一个非公平的可重入锁 */
    ReentrantLock lock = new ReentrantLock();

    public void syncF1() {
        lock.lock();
        lock.lock();
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getId() + " 进入 syncF1()");
            syncF2();
            System.out.println(Thread.currentThread().getId() + " 退出 syncF1()");
        } finally {
            lock.unlock();
            lock.unlock();
            lock.unlock(); /* 当加锁、解锁操作不匹配时，发生死锁 */
        }
    }

    public void syncF2() {
        lock.lock();
        lock.lock();
        try {
            System.out.println("\t" + Thread.currentThread().getId() + " 进入 syncF2()");
        } finally {
            lock.unlock();
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ReentrantLockPractice3 r = new ReentrantLockPractice3();

        new Thread(() -> {
            r.syncF1();
        }).start();

        new Thread(() -> {
            r.syncF1();
        }).start();
    }
}
/*
当加锁、解锁次数相同时，无任何问题（可 重 入 锁）；
当加锁、解锁操作不匹配时，发生 死 锁！

对于死锁，可以使用 jsp + jstack 进行排查、解决
 */

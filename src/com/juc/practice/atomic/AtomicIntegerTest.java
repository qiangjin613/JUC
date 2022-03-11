package com.juc.practice.atomic;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 使用 AtomicInteger 解决 VolatileTest.java 中使用 volatile 修饰变量的原子性问题
 */
public class AtomicIntegerTest {

    private AtomicInteger atomicInteger = new AtomicInteger();

    public void addNumber() {
        atomicInteger.getAndIncrement();
    }

    public static void main(String[] args) {
        AtomicIntegerTest at = new AtomicIntegerTest();
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    at.addNumber();
                }
            }, String.valueOf(i)).start();
        }

        /* 等待上面 20 个线程都跑完后（此时，在默认下，只有一个 main 和 GC 线程），再用 main线程 输出变量 number 的值 */
        while (Thread.activeCount() > 2) {
            Thread.yield();
        }

        System.out.println(Thread.currentThread().getName() + " finally number value: " + at.atomicInteger);
    }
}

/**
 * AtomicXxxx 中的 CAS
 */
class AtomicCASTest {
    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger(5);

        // do sth.

        System.out.println(atomicInteger.compareAndSet(5, 2022) + " current data: " + atomicInteger.get());
        System.out.println(atomicInteger.compareAndSet(5, 2023) + " current data: " + atomicInteger.get());
    }
}

/**
 * CAS 中的 ABA 问题
 */
class ABAQuestion {
    public static void main(String[] args) {
        AtomicInteger a = new AtomicInteger();

        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " " + a.compareAndSet(0, 1) + " current value: " + a.get());
            System.out.println(Thread.currentThread().getName() + " " + a.compareAndSet(1, 0) + " current value: " + a.get());
        }, "偷梁换柱之 A 线程").start();

        new Thread(() -> {
            try {
                /* 这个睡眠操作的目的是让 A 线程执行完毕 */
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " " + a.compareAndSet(0, 20) + " current value: " + a.get());
        }, "B").start();
    }
}

/**
 * 使用带时间戳的原子引用 AtomicStampedReference<V> 解决 CAS 中的 ABA 问题
 */
class ABASolve {
    public static void main(String[] args) {
        AtomicStampedReference<Integer> asr = new AtomicStampedReference<>(0, 1);

        new Thread(() -> {
            int stamp = asr.getStamp();
            System.out.println(Thread.currentThread().getName() + " 第一次版本号：" + stamp);
            try {
                /* 等到 B 线程也拿到同样版本号的数值 */
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(
                    Thread.currentThread().getName() +
                            " " + asr.compareAndSet(0, 1, asr.getStamp(), asr.getStamp() + 1)
                            + " current value: " + asr.getReference() + ", stamp: " + asr.getStamp());
            System.out.println(
                    Thread.currentThread().getName() +
                            " " + asr.compareAndSet(1, 0, asr.getStamp(), asr.getStamp() + 1)
                            + " current value: " + asr.getReference() + ", stamp: " + asr.getStamp());
        }, "偷梁换柱之 A 线程").start();

        new Thread(() -> {
            int stamp = asr.getStamp();
            System.out.println(Thread.currentThread().getName() + " 第一次版本号：" + stamp);
            try {
                /* 这个睡眠操作的目的是让 A 线程执行完毕 */
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName()
                    + " " + asr.compareAndSet(0, 20, stamp, stamp + 1)
                    + " current value: " + asr.getReference() + ", stamp: " + asr.getStamp());
        }, "B").start();
    }
}

package com.juc.practice.keyword;

/**
 * volatile 的可见性测试：
 *  1. 添加、去掉 volatile 查看变量的可见性对线程的影响
 */
class VolatileTest {

    private volatile int number = 0;

    public void addNumber() {
        number = 100;
    }

    public static void main(String[] args) {
        VolatileTest vt = new VolatileTest();

        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " come in");
            vt.addNumber();
            System.out.println(Thread.currentThread().getName() + " data.num = " + vt.number);
        }, "A").start();

        while (vt.number == 0);

        System.out.println(Thread.currentThread().getName() + " data.num = " + vt.number);
    }
}

/**
 * volatile 不保证原子性的测试：
 *  通过当前实例，可以看到，因为 volatile 不保证原子性，所以其输出结果不一定是 20000。
 */
class AtomicTest {

    private volatile int number = 0;

    public void addNumber() {
        number++;
    }

    public static void main(String[] args) {
        AtomicTest at = new AtomicTest();
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

        System.out.println(Thread.currentThread().getName() + " finally number value: " + at.number);
    }
}
/*
【解决】
方式1：synchronized：在 addNumber() 上添加
方式2：AtomicXxx 原子类：这里可以使用 AtomicInteger 替代变量 number
 */

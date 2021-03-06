package com.juc.practice.thread.create;

/**
 * 通过 Runnable 创建线程
 */
public class ImplementsRunnableCreate implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            /* 因为这里没有继承 Thread 类，所以只能使用以下方法获取当前线程 */
            System.out.println(Thread.currentThread().getName() + " " + i);
        }
    }

    public static void main(String[] args) {
        /* 获取主线程信息 */
        System.out.println(Thread.currentThread().getName());

        /* 创建一个新线程 */
        ImplementsRunnableCreate st = new ImplementsRunnableCreate();
        /* 可以看到，实际的线程对象依旧是 Thread 对象。Runnable 实际上只是 @FunctionalInterface 修饰的函数式接口*/
        new Thread(st).start();
    }
}

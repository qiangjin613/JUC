package com.juc.practice.thread.create;

/**
 * 通过继承 Thread 创建线程
 */
public class ExtendThreadCreate extends Thread {

    private int i;

    @Override
    public void run() {
        for (; i < 100; i++) {
            /* 等价于 this.getName()、Thread.currentThread().getName() */
            System.out.println(getName() + " " + i);
            /* 运行过程中修改线程名 */
            if (i == 30) {
                setName("小兵");
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            /* 使用 Thread.currentThread() 调用当前线程 */
            System.out.println(Thread.currentThread().getName() + " " + i);

            if (i == 20) {
                new ExtendThreadCreate().start();
                new ExtendThreadCreate().start();
            }
        }
    }
}

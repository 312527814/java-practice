package com.my._4_tools;

import org.junit.Test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class _3_SemaphoreTest {
    @Test
    public void test() throws Exception {
        Semaphore semaphore = new Semaphore(4);
        for (int i = 0; i < 13; i++) {
            new Thread(() -> {
                try {
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName() + ": after acquire");
                    Thread.sleep(1000 * 5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();
                    System.out.println(Thread.currentThread().getName() + ": release");
                }
            }, i + "").start();
        }

        System.in.read();
    }

    @Test
    public void testMy() throws Exception {
        Object a = new Object();
        Object b = new Object();
        new Thread(() -> {
            synchronized (a) {
                System.out.println("a......");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (b){
                    System.out.println("......");
                }
            }
        },"a").start();

        new Thread(() -> {
            synchronized (b) {
                System.out.println("b......");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (a){
                    System.out.println("......");
                }
            }
        },"b").start();

        System.in.read();
    }

    @Test
    public void testMySemaphore() throws Exception {
        MySemaphore semaphore = new MySemaphore(4);
        for (int i = 0; i < 13; i++) {
            new Thread(() -> {
                try {
//                    System.out.println(Thread.currentThread().getName() + ": before acquire");
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName() + ": after acquire "+System.currentTimeMillis());
                    Thread.sleep(1000 * 10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
//                    if (Thread.currentThread().getName().equals("1"))
                    semaphore.release();
                }
            }, i + "").start();

        }

        Thread.sleep(10000);
        System.in.read();
    }

}

class MySemaphore extends AbstractQueuedSynchronizer {


    public MySemaphore(int count) {
        setState(count);
    }

    @Override
    protected boolean tryRelease(int arg) {
        int state = getState();
//        System.out.println(Thread.currentThread().getName() + ": tryRelease state   " + state);
        return true;
    }

    @Override
    protected boolean tryAcquire(int arg) {
        int state = getState();
        return state > 0;
    }

    public void acquire() {
        acquire(1);
        for (; ; ) {
            int state = getState();
            boolean b = compareAndSetState(state, state - 1);
            if (b) {
                break;
            }
        }

    }

    public void release() {


        System.out.println(Thread.currentThread().getName() + ": after release  "+System.currentTimeMillis());

        for (; ; ) {
            int state = getState();
            boolean b = compareAndSetState(state, state + 1);
//            System.out.println(Thread.currentThread().getName() + ": release  " + b + System.currentTimeMillis());
            if (b) {
                break;
            }
        }

        boolean release = release(1);
    }
}

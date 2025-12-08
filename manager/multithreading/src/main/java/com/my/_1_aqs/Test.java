package com.my._1_aqs;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Test {
//AbstractQueuedLongSynchronizer

    public static void main2(String[] args) throws InterruptedException {


        ReentrantLock lock = new ReentrantLock(false);
        Condition condition = lock.newCondition();
        condition.await();
        new Thread(() -> {
            lock.lock();
        }).start();

        Thread.sleep(1000);
        new Thread(() -> {

            lock.lock();
        }).start();

        ReentrantReadWriteLock lock1 = new ReentrantReadWriteLock();
        ReentrantReadWriteLock.ReadLock readLock = lock1.readLock();
        ReentrantReadWriteLock.WriteLock writeLock = lock1.writeLock();
        readLock.lock();
        writeLock.lock();
    }

    public static void main(String[] args) throws InterruptedException {


        ReentrantLock lock = new ReentrantLock();
        new Thread(() -> {
           synchronized (lock){
               try {
                   Thread.sleep(1000*5);
                   System.out.println("........1 start........");
                   lock.notify();
                   lock.wait();
                   System.out.println("........1 wait end.......");
                   Thread.sleep(1000*5);

                   System.out.println("........1 end........");
                   lock.notify();
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }

           }
        }).start();

        Thread.sleep(1000);
        new Thread(() -> {
            synchronized (lock){
                try {

                    System.out.println("........2 start........");
                    lock.notify();
                    lock.wait();
                    lock.notify();
                    System.out.println("........2 end........");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();

        System.out.println(".....end......");
        new CountDownLatch(1).await();

    }
}

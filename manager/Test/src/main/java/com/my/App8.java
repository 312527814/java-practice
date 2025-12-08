package com.my;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class App8 {
    public  ThreadPoolExecutor executor2 = new ThreadPoolExecutor(
            5,
            50,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1),
            new ThreadFactory(){
                private  final AtomicInteger poolNumber = new AtomicInteger(0);
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("mytest-thread-"+ poolNumber.incrementAndGet());

                    return  thread;
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    public static void main(String[] args) throws InterruptedException {
        try {
            App8 app8 = new App8();
            app8.ss();
        }catch (Exception e){
            e.printStackTrace();
        }
        new CountDownLatch(1).await();
    }

    public void ss(){
        for (int i = 0; i < 10; i++) {
            executor2.execute(() -> {
               new App8().print();
            });
        }
    }
    public void print(){
        System.out.println("........"+ Thread.currentThread().getName());
        try {
            Thread.sleep(1000);
            new CountDownLatch(1).await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

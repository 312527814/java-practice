package com.my;

import org.omg.CORBA.PUBLIC_MEMBER;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class App6 {
    public ThreadPoolExecutor executor = new ThreadPoolExecutor(
            100,
            100,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(4),
            Executors.defaultThreadFactory(),
            new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy()
    );

    @Override
    protected void finalize() throws Throwable {
        System.out.println("fin................");
        executor.shutdown();
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 20; i++) {
            if (i < 10) {
                test();
            }else {
                System.gc();
            }
            main2();
            Thread.sleep(1000);
        }
    }

    public static void test() {
        App6 app6 = new App6();
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            app6.executor.submit(() -> {
                int a = 1;
            });
        }
    }

    public static void main2() {
        int activeThreadCount = Thread.getAllStackTraces().size();
        System.out.println("当前活动线程数量: " + activeThreadCount);
    }
}

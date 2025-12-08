package com.my;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.concurrent.*;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    public void ss() throws InterruptedException {


        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                2,
                2,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(40),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        for (int i = 0; i < 20; i++) {
            executor.execute(() -> {
                try {
                    Thread.sleep(10 * 1000);
                    System.out.println("...........");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            });
        }
        new CountDownLatch(1).await();
    }
    @Test
    public void ssss() throws InterruptedException {
//        return new ScheduledThreadPoolExecutor(2,
//                new BasicThreadFactory.Builder().namingPattern("schedule-pool-%d").daemon(true).build(),
//                new ThreadPoolExecutor.CallerRunsPolicy())
        ScheduledThreadPoolExecutor  executor=  new ScheduledThreadPoolExecutor(
                2,
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        for (int i = 0; i < 20; i++) {
            executor.schedule(() -> {
                    System.out.println("...........");

            },10,TimeUnit.SECONDS);
        }
        new CountDownLatch(1).await();
    }
}

package com.my._6_forkjoin;

import java.io.IOException;
import java.util.concurrent.*;

public class ForkJoinTest {

    private static volatile int count = 0;

    private static void setCount() {
        count++;
    }

    public static void main(String[] args) throws Exception {
        int ii = 19 / 2;

        long[] longs = new long[10];
        for (int i = 0; i < longs.length; i++) {
            longs[i] = i;
        }
        long l = forkJoinSum(longs);

        Thread.sleep(2000 * 2000);

    }


    private static long forkJoinSum(long[] arr) throws ExecutionException, InterruptedException, IOException {
        long start = System.currentTimeMillis();

        ForkJoinPool forkJoinPool1 = new ForkJoinPool(4);
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
//        System.out.println("read1..");
//        System.in.read();
        // 提交任务
        ForkJoinTask<Long> forkJoinTask = forkJoinPool.submit(new SumTask(arr, 0, arr.length));
        System.out.println("read2..");
        System.in.read();
        // 获取结果
        Long sum = forkJoinTask.get();
        System.out.println("sum: " + sum);
        System.in.read();
        forkJoinPool.shutdown();

        System.out.println("sum: " + sum);
        System.out.println("fork join elapse: " + (System.currentTimeMillis() - start));

        return sum;
    }

    private static class SumTask extends RecursiveTask<Long> {
        private long[] arr;
        private int from;
        private int to;

        public SumTask(long[] arr, int from, int to) {
            this.arr = arr;
            this.from = from;
            this.to = to;
        }

        @Override
        protected Long compute() {

            System.out.println("Thread.currentThread().getName() =>" + Thread.currentThread().getName());
            // 小于1000的时候直接相加，可灵活调整
            if (to - from <= 1) {
                long sum = 0;
                for (int i = from; i < to; i++) {
                    // 模拟耗时
//                    sum += (arr[i]/3*3/3*3/3*3/3*3/3*3);
                    sum += (arr[i]);

                }

//                try {
//                    Thread.sleep(1000 * 10);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                return sum;
            }
            synchronized (ForkJoinTest.class) {
                System.out.println("count: " + count++);
            }
            try {
                Thread.sleep(1000 * 25);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("from :" + from + " to :" + to);

            // 分成两段任务，本文由公从号“彤哥读源码”原创
            int middle = (from + to) / 2;
            SumTask left = new SumTask(arr, from, middle);
            SumTask right = new SumTask(arr, middle, to);

            // 提交左边的任务
            left.fork();
            // 右边的任务直接利用当前线程计算，节约开销
            Long rightResult = right.compute();
            // 等待左边计算完毕
            Long leftResult = left.join();
            // 返回结果
            return leftResult + rightResult;
        }
    }


    public static void ff() {

        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4,
                60, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(4),
                r -> {
                    Thread thread = new Thread(r);
                    thread.setName("myThreadPoolExecutor");
                    return thread;
                });

        for (int i = 0; i < 5; i++) {
            executor.execute(() -> {
                while (true) {
                    try {
                        Thread.sleep(1000 * 5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("dedede");
                }
            });
        }


    }
}

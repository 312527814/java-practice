package com.my._5_threadpool;

import org.junit.Test;

import javax.xml.bind.SchemaOutputResolver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.*;

public class CompletableFutureTest {
    public static void main(String[] args) throws Exception {


        ExecutorService executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 10,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                r -> {
                    Thread thread = new Thread(r);
//                    thread.setName("处理点击量的线程池");
                    return thread;
                }
        );
        CompletableFuture.runAsync(()->{
            System.out.println("ddd");
        });
//        CompletableFuture<String> voidCompletableFuture = CompletableFuture.completedFuture("dss");

        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("runAsync=" + Thread.currentThread().getName() + "|" + Thread.currentThread().isDaemon());
        }, executor);
        voidCompletableFuture.thenRunAsync(() -> {
            System.out.println("thenRunAsync=" + Thread.currentThread().getName() + "|" + Thread.currentThread().isDaemon());
        }, executor);
//        String s = voidCompletableFuture.get();
//        System.out.println("s=" + s);
        Void aVoid = voidCompletableFuture.get();
        System.out.println("done=" + voidCompletableFuture.isDone());
        TimeUnit.SECONDS.sleep(4);
        System.out.println("done=" + voidCompletableFuture.isDone());


        System.in.read();


    }

    public void test() throws Exception {
        CompletableFuture<String> completableFuture = new CompletableFuture<String>();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(3);
                    System.out.println(Thread.currentThread().getName() + "执行.....");
                    completableFuture.complete("success");//在子线程中完成主线程completableFuture的完成

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread t1 = new Thread(runnable);
        t1.start();//启动子线程

        String result = completableFuture.get();//主线程阻塞，等待完成
        System.out.println(Thread.currentThread().getName() + "1x:  " + result);


    }

    /**
     * exceptionally 只会接受第一个抛出的异常
     */
    @Test
    public void ss() {
        ExecutorService executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 10,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                r -> {
                    Thread thread = new Thread(r);
                    thread.setName("处理点击量的线程池");
                    return thread;
                }
        );
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(".....1:" + Thread.currentThread().getName());
        }, executor);

        CompletableFuture<Void> voidCompletableFuture2 = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(3000);
                sss1();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(".....2:" + Thread.currentThread().getName());
        }, executor);

        CompletableFuture<Void> voidCompletableFuture3 = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(1000);
                sss3();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(".....3:" + Thread.currentThread().getName());
        }, executor);
        CompletableFuture.allOf(voidCompletableFuture, voidCompletableFuture3, voidCompletableFuture2).whenComplete((a, b) -> {
            System.out.println("whenComplete");
//            sss2();
        }).exceptionally(e -> {
            e.printStackTrace();
            try {
                Thread.sleep(3000);

            } catch (InterruptedException ee) {
                ee.printStackTrace();
            }
            //System.out.println(e.toString());
            return null;
        }).join();
       /* voidCompletableFuture3.exceptionally(e ->
        {
            e.printStackTrace();
            return null;
        });*/
        System.out.println("end");


    }

    /**
     * 获取多个线程的异常
     */
    @Test
    public void ssw() {
        ExecutorService executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 10,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                r -> {
                    Thread thread = new Thread(r);
                    thread.setName("处理点击量的线程池");
                    return thread;
                }
        );
        ArrayList<CompletableFuture<Void>> objects = new ArrayList<>();
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(".....1:" + Thread.currentThread().getName());
        }, executor);

        CompletableFuture<Void> voidCompletableFuture2 = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(3000);
                //sss1();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(".....2:" + Thread.currentThread().getName());
        }, executor);

        CompletableFuture<Void> voidCompletableFuture3 = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(3000);
                sss2();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(".....3:" + Thread.currentThread().getName());
        }, executor);
        objects.add(voidCompletableFuture);
        objects.add(voidCompletableFuture2);
        objects.add(voidCompletableFuture3);
        CompletableFuture.allOf(voidCompletableFuture, voidCompletableFuture2, voidCompletableFuture3).whenComplete((a, b) -> {
            System.out.println("whenComplete");
        }).exceptionally(e -> {
            objects.forEach(f -> {
                if(f.isCompletedExceptionally()){
                    f.exceptionally(ee -> {
                        ee.printStackTrace();
                        System.out.println(ee);
                        return null;
                    });
                }

            });
            return null;
        }).join();
        System.out.println("end");


    }

    @Test
    public void ssw2() {
        ExecutorService executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 10,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                r -> {
                    Thread thread = new Thread(r);
                    thread.setName("处理点击量的线程池");
                    return thread;
                }
        );
        CompletableFuture<Void>[] objects = new CompletableFuture[3];
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(".....1:" + Thread.currentThread().getName());
        }, executor);

        CompletableFuture<Void> voidCompletableFuture2 = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(3000);
                //sss1();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(".....2:" + Thread.currentThread().getName());
        }, executor);

        CompletableFuture<Void> voidCompletableFuture3 = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(3000);
                sss2();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(".....3:" + Thread.currentThread().getName());
        }, executor);
        objects[0]=voidCompletableFuture;
        objects[1]=voidCompletableFuture2;
        objects[2]=voidCompletableFuture3;
        CompletableFuture.allOf(objects).whenComplete((a, b) -> {
            System.out.println("whenComplete");
        }).exceptionally(e -> {
            for (CompletableFuture<Void> f : objects) {
                if(f.isCompletedExceptionally()){
                    f.exceptionally(ee -> {
                        ee.printStackTrace();
                        System.out.println(ee);
                        return null;
                    });
                }
            }
            return null;
        }).join();
        System.out.println("end");


    }
    private void sss1() {
        if (1 > 0) {
            throw new RuntimeException("dsdsds11111");
        }
    }
    private void sss2() {
        if (1 > 0) {
            throw new RuntimeException("dsdsds22222");
        }
    }
    private void sss3() {
        if (1 > 0) {
            throw new RuntimeException("wwwww33333");
        }
    }

    public static void main2(String[] args) {
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
    }
}

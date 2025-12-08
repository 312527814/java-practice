import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 */
public class App {

    public static int cout = 1;

    public static void main(String[] args) throws InterruptedException {

        ThreadPoolExecutor executor = new ThreadPoolExecutor(0, Runtime.getRuntime().availableProcessors() * 2, 2, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1)
                , new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("thread_name_" + cout++);
                return thread;
            }
        }, new ThreadPoolExecutor.CallerRunsPolicy());


        executor.execute(() -> {
            while (true) {
                System.out.println(Thread.currentThread().getName() + ":" + new Date());
            }
        });
        executor.execute(() -> {
            while (true) {
                System.out.println(Thread.currentThread().getName() + ":" + new Date());
            }
        });
        executor.execute(() -> {
            while (true) {
                System.out.println(Thread.currentThread().getName() + ":" + new Date());
            }
        });

    }


}

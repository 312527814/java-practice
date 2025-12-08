package com.my;

import java.util.concurrent.*;

public class App7 {
    public static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

    public static void main(String[] args) throws Exception {
        connect();
        System.out.println("end");
    }

    static int i = 0;

    public static void connect() {

        ScheduledFuture schedule = executor.schedule(new ConnectTask("", i), 2, TimeUnit.SECONDS);
        try {
            i++;
            schedule.get();
        } catch (Exception e) {
            connect();
        }
    }

    private static class ConnectTask implements Callable<String> {
        private String url;
        private int delay;

        public ConnectTask(String url, int delay) {
            this.url = url;
            this.delay = delay;
        }

        @Override
        public String call() throws Exception {
            System.out.println(".............");
            if (delay < 10) {
                throw new Exception("ssss");
            }
            return null;
        }
    }
}




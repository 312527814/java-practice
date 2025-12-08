package com.my;

import com.my.aspectjaop.MyService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Hello world!
 */
public class App2 {
    private static List<App2> list = new ArrayList<>();

    private static ThreadLocal<App2> local = new ThreadLocal<>();

    public static void main(String[] args) throws Exception {

        App2 app2 = new App2();
        Class<? extends App2> aClass = app2.getClass();
        Class<App2> app2Class = App2.class;


        System.out.println(aClass);
        System.out.println(app2Class);

//        App2 app = new App2();
//        while (true) {
//            local.set(app);
//        }

        Object lock = getLock("1", "1");

        new Thread(() -> {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("end........");

            }
        }).start();

        Thread.sleep(100);
        synchronized (lock) {
            lock.notifyAll();

            System.out.println("end2........");

        }



    }

    private static HashMap<String, HashMap<String, Object>> locks = new HashMap<>();

    public static Object getLock(String type, String id) {
        HashMap<String, Object> hashMap = locks.get(type);
        if (hashMap == null) {
            synchronized (App2.class) {
                hashMap = new HashMap<>();
                locks.put(type, hashMap);
            }
        }
        Object s = hashMap.get(id);
        if (s == null) {
            synchronized (hashMap) {
                if (s == null) {
                    hashMap.put(id, new Object());
                    s = id;
                }
            }
        }
        return s;
    }

    public void test(int id) {
        //todo
        synchronized (App2.class) {
            // todo 扣除id商品的库存
        }
        //todo
    }


}

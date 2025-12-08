package com.my;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 描 述: <br/>
 * 作 者: liuliang14<br/>
 * 创 建:2022年06月22⽇<br/>
 * 版 本:v1.0.0<br> *
 * 历 史: (版本) 作者 时间 注释 <br/>
 */
public class RedisLock {

    /*Redisson的配置类*/
    private static RedissonClient redissonClient() {
        Config config = new Config();
        /* Redis 单节点*/
        config.useSingleServer().setAddress("redis://localhost:7901");
        config.useSingleServer().setDatabase(6);
        config.useSingleServer().setPassword("Gsafety.123");
        return Redisson.create(config);
    }

     public static void main2(String[] args) throws Exception {
         RedissonClient redissonClient = redissonClient();
         RLock lock = redissonClient.getLock("aaa");
//         new Thread(()->{
//             System.out.println("...........id:.."+Thread.currentThread().getId()+"........");
//             lock.lock();
//             System.out.println(".............unlock........");
//         }).start();
//         Thread.sleep(100000);

         System.out.println("...........master id:.."+Thread.currentThread().getId()+"........");
         lock.lock(1000*30, TimeUnit.SECONDS);
//         lock.lock();

         System.out.println(".............unlock2.......");

     }
    public static void main(String[] args) throws InterruptedException {
        RedissonClient redissonClient = redissonClient();
        RLock lock = redissonClient.getLock("aaa");
        lock.lock();

        new Thread(()->{
            RLock lock2 = redissonClient.getLock("aaa");
            System.out.println("222.....");
            lock2.lock(100, TimeUnit.SECONDS);
            System.out.println("222");
            lock2.unlock();
        }).start();

        Thread.sleep(1000*10);
        lock.unlock();

        System.out.println("end");

        new CountDownLatch(1).await();

//        lock2.lock(10,TimeUnit.SECONDS);

        //Thread.sleep(10000);
//        lock.unlock();
    }
}

package com.my.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager;
import org.springframework.stereotype.Component;

@Component
public class HystrixTest {
    @HystrixCommand(
            fallbackMethod="back",
//            groupKey = "a",
//            commandKey = "b",
//            threadPoolKey = "c",
            commandProperties = {
                    // 是否开启熔断器
                    @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, value = "2"),      // 统计时间窗内请求次数
                    @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS, value = "100000"), // 休眠时间窗口
                    @HystrixProperty(name = HystrixPropertiesManager.METRICS_ROLLING_STATS_TIME_IN_MILLISECONDS, value = "10000") // 统计时间窗
            },threadPoolProperties = {
            @HystrixProperty(name = "coreSize", value = "1"),
            @HystrixProperty(name = "maxQueueSize", value = "1"),
            @HystrixProperty(name = "keepAliveTimeMinutes", value = "2"),
            @HystrixProperty(name = "queueSizeRejectionThreshold", value = "1"),
            @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "1"),
            @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1440")})
    public String alive(int a) {

//        logger.info("begin  check/web  ................");
        if (a == 2) {
            System.out.println("........................"+ "Thread.currentThread().getName():"+Thread.currentThread().getName());
            int b = a / 0;
        }
        System.out.println("hello wold....");
        System.out.println("Thread.currentThread().getName():"+Thread.currentThread().getName());
        return "hello wold";
    }

    @HystrixCommand(
            fallbackMethod="back",
//            groupKey = "a",
//            commandKey = "bb",
//            threadPoolKey = "c",
            commandProperties = {
                    // 是否开启熔断器
                    @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, value = "2"),      // 统计时间窗内请求次数
                    @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS, value = "100000"), // 休眠时间窗口
                    @HystrixProperty(name = HystrixPropertiesManager.METRICS_ROLLING_STATS_TIME_IN_MILLISECONDS, value = "10000") // 统计时间窗
            },threadPoolProperties = {
            @HystrixProperty(name = "coreSize", value = "1"),
            @HystrixProperty(name = "maxQueueSize", value = "1"),
            @HystrixProperty(name = "keepAliveTimeMinutes", value = "2"),
            @HystrixProperty(name = "queueSizeRejectionThreshold", value = "1"),
            @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "1"),
            @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1440")})
    public String alive2(int a) {

//        logger.info("begin  check/web  ................");
        if (a == 2) {
            System.out.println("........................"+ "Thread.currentThread().getName():"+Thread.currentThread().getName());
            int b = a / 0;
        }
        System.out.println("hello wold....");
        System.out.println("Thread.currentThread().getName():"+Thread.currentThread().getName());
        return "hello wold";
    }

    public String back(int a) {

        System.out.println("fail Thread.currentThread().getName():"+Thread.currentThread().getName());
        return "fail error:";
    }
}

package com.my.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

@RestController
@Controller
public class TestController {
    Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    private HystrixTest hystrixTest;

    @GetMapping("check/web")
    public String alive(int a) {

//        logger.info("begin  check/web  ................");
//        System.out.println("hello wold....");
//        logger.info("end  check/web  ................");
        System.out.println("TestController Thread.currentThread().getName():"+Thread.currentThread().getName());
        String alive = hystrixTest.alive(a);
        return alive;
    }

    @GetMapping("check/webb")
    public String alive2(int a) {

//        logger.info("begin  check/web  ................");
//        System.out.println("hello wold....");
//        logger.info("end  check/web  ................");
        System.out.println("TestController Thread.currentThread().getName():"+Thread.currentThread().getName());
        String alive = hystrixTest.alive2(a);
        return alive;
    }

    @GetMapping("check/web2")
    public String alive(@RequestParam("id") String id) {
        throw new RuntimeException("id=" + id);
    }

    @PostMapping("check/web")
    public String alive2() {
        System.out.println("hello wold....");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int a = 1 / 0;
        return "hello wold post";
    }

    @PostMapping("check/post")
    public String post(@RequestBody String input) {
        System.out.println("hello wold....");
        return "hello wold post input=" + input;
    }
}

package com.my.controller;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;


@RestController
@RequestMapping("/Test01")
public class Test01Controller {

    int i = 0;
    @Resource
    Environment environment;

    private List<Person> peoples=new ArrayList<>();

    private List<byte[]> bytes=new ArrayList<>();

    public ThreadPoolExecutor executor = new ThreadPoolExecutor(
            1,
            1,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(4),
            Executors.defaultThreadFactory(),
            new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy()
    );



    @GetMapping("index")
    public String index(int id) {

        executor.execute(()->{
            for (int i = 0; i < id; i++) {
                byte[] bigArray = new byte[1024 * 10];
                bytes.add(bigArray);
                System.out.println(bytes.size()+"..........");
            }
        });
        return "hello index+";
    }

    @GetMapping("index2")
    public String index() {

        for (int j = 0; j < 10; j++) {
            Person person = new Person();
            person.setId(j);
            peoples.add(person);
            if (j % 2 == 0) {
                Test.catchs.add(person);
            }

        }
        return peoples.size() + " "+Test.catchs.size();
    }

    private String getBrowserVersion(HttpServletRequest request) {
        String header = request.getHeader("User-Agent");

        UserAgent userAgent = UserAgent.parseUserAgentString(header);

        Browser browser = userAgent.getBrowser();

        Version version = browser.getVersion(header);

        return version.getVersion();
    }

    private HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }


}

package com.my.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.nacos.api.exception.NacosException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

@Controller
@RequestMapping("test")
@RefreshScope
public class TestController {

    private String baseUrl = "http://123/api";

    @Resource(name = "restTemplate")
    private RestTemplate template;
    @Resource(name = "restTemplate2")
    private RestTemplate template2;

    @Value("${a}")
    private String a;

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ResponseBody
    public String get() {

//        String url = "/discovery/get?serviceName=123";
        String forObject1 = template2.getForObject("http://localhost:8085" + "/test/get2", String.class);

        String forObject = template.getForObject("http://my-producer/" + "/test/get2", String.class);

        return forObject.toString();
    }

    @RequestMapping(value = "/get2", method = RequestMethod.GET)
    @ResponseBody
    public String get2() {

        long l = System.currentTimeMillis();

        System.out.println("get2。。。。。。。。。。。。。"+a);
        return "get2";
    }
}
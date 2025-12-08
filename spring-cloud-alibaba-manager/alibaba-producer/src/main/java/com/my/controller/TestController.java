package com.my.controller;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RefreshScope
public class TestController {
    @Autowired
    NacosConfigProperties nacosConfigProperties;

    @Value("${a}")
    private String a;

    @GetMapping("/get")
    public String get(){
        System.out.println("..........."+a);
//        int a = 1 / 0;
        return "hello word producer";
    }
}

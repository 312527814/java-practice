package com.my.controller;

import com.my.openfeign.ProducerFegin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/test")
public class TestController {


    @Resource
    private ProducerFegin producerFegin;
    @GetMapping("/get")
    public String get(){
        String s = producerFegin.checkWeb();
        return "consumer:"+s;
    }
}

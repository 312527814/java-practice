package com.my.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/my")
public class MyController {
    @GetMapping("/get")
    public String get(){
        System.out.println("...........");
//        int a = 1 / 0;
        return "hello word my";
    }
}

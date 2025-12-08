package com.my.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class My1Controller {

    @Autowired
    private MyTest test;

    @GetMapping("my/index")
    public String index() {

        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            System.out.println("hello index+"+hostAddress);
            return "hello index+"+hostAddress;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "hello index+";
    }

    @GetMapping("my/index2")
    public void index2(HttpServletResponse response) {

        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            System.out.println("hello index+"+hostAddress);

            response.setHeader("Connection", "close");
            // 发送响应数据
            response.getWriter().write("This connection will be closed after this response."+hostAddress);
//            return "hello index+"+hostAddress;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        return "hello index+";
    }


}

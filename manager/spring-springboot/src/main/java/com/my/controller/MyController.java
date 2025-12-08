package com.my.controller;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
//@RestController
public class MyController {

    int i = 0;
    @Resource
    Environment environment;

    @GetMapping("index")
    public String index() {

        Person person = new Person();
        System.out.println("load:"+person.getClass().getClassLoader());
        System.out.println("currentTheard=>" + Thread.currentThread().getName());
        String port = environment.getProperty("server.port");
        return "hello index+" + port;
    }

    @RequestMapping(value = "/param", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getFormatData() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "zhangsan");
        map.put("age", 22);
        map.put("date", new Date());
        return map;
    }
    @RequestMapping(value = "/paramt", method = RequestMethod.GET)
    public Map<String, Object> paramt() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "zhangsan");
        map.put("age", 22);
        map.put("date", new Date());
        return map;
    }

    @RequestMapping(value = "/redirect", method = RequestMethod.GET)
    public String redirect() {
        return "redirect:http://www.baidu.com";
    }
}

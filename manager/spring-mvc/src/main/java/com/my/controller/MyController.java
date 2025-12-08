package com.my.controller;

import com.my.model.Person;
import com.my.service.MyService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/my")
public class MyController implements BeanFactoryAware {
    @Autowired
    private MyService myService;

    @Value("${id}")
    private String id;

    public MyController(){
        System.out.print("MyController。。。。。。。。。。。。");
    }

    private BeanFactory beanFactory;

    @GetMapping("/get")
    public String get() {

        System.out.print("get。。。。。。。。。。。。");
        return "/index.jsp";
    }

    @GetMapping("/default")
    public String defaults() {

        System.out.print("default。。。。。。。。。。。。");
        return "/default.jsp";
    }

    @ResponseBody
    @GetMapping("/param")
    public String param(int id, String name) {

        System.out.print(id + name);
        return "/default.jsp";
    }

    @ResponseBody
    @GetMapping("/json")
    public String json(int id, String name) {

        Thread thread = new Thread(() -> {
            Person person = new Person();
            person.setId(id);
            person.setName(name);
            System.out.println("person.getClass().getClassLoader():" + person.getClass().getClassLoader());
        },"json");
        thread.start();
        ClassLoader contextClassLoader = thread.getContextClassLoader();
        System.out.println("contextClassLoader:"+contextClassLoader);

        return "person.toString()";
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}

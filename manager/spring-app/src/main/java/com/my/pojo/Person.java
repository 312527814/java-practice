package com.my.pojo;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class Person implements InitializingBean, DisposableBean {

    public Person() {
        System.out.print("person");
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private Integer id;
    private String name;

//    public void afterPropertiesSet() {
//        System.out.print("afterPropertiesSet");
//    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.print("afterPropertiesSet");
    }

    @Bean
    public Test getTest(){

        return  new Test();
    }

    @Bean
    public Test getTest2(){

        return  getTest();
    }

    @Override
    public void destroy() throws Exception {
        System.out.println(".....destroy....");
    }
}

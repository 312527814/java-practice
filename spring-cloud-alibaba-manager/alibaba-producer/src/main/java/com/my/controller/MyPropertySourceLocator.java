package com.my.controller;

import org.springframework.boot.DefaultPropertiesPropertySource;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class MyPropertySourceLocator implements PropertySourceLocator {
    public MyPropertySourceLocator(){
        System.out.println("MyPropertySourceLocator........");
    }
    @Override
    public PropertySource<?> locate(Environment environment) {

        Map<String, Object> source=new HashMap<>();
        source.put("aa",123);
        PropertySource propertySource = new DefaultPropertiesPropertySource(source);

        return propertySource;
    }
}

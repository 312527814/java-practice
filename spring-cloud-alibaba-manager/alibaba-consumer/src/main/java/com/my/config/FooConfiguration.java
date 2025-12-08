package com.my.config;

import feign.Feign;
import feign.Target;
import org.springframework.cloud.openfeign.CircuitBreakerNameResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.lang.reflect.Method;

@Configuration
public class FooConfiguration {
//    @Bean
//    @Scope("prototype")
//    public Feign.Builder feignBuilder() {
//        return Feign.builder();
//    }
//
//    @Bean
//    public CircuitBreakerNameResolver circuitBreakerNameResolver() {
//        return (String feignClientName, Target<?> target, Method method) ->{
//            System.out.println("11111111111");
//            return feignClientName + "_" + method.getName();
//
//        } ;
//    }
}

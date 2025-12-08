package com.my.config;


import com.my.filter.MyTracingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program:
 * @description:
 * @author: liang.liu
 * @create: 2021-10-12 16:08
 */
@Configuration
public class WebMvcConfig {

//    @Bean
//    public FilterRegistrationBean pubOperationLogFilter(MyTracingFilter myTracingFilter) {
//
//        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
//
//        filterRegistrationBean.setFilter(myTracingFilter);
//        //配置过滤规则
//        filterRegistrationBean.addUrlPatterns("/*");
//        //设置过滤器名称
//        filterRegistrationBean.setName("myFilter");
//        //执行次序,值越小，则越早执行
//        filterRegistrationBean.setOrder(100);
//
//        return filterRegistrationBean;
//    }
}


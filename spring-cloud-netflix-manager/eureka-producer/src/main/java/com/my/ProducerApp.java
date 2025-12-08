package com.my;

import com.my.servlet.MyServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;

/**
 * Hello world!
 */
@EnableHystrix
@SpringBootApplication
public class ProducerApp {
    public static void main(String[] args) {
        SpringApplication.run(ProducerApp.class, args);
    }

//    @Bean
//    public ServletRegistrationBean servletRegistrationBean() {
//        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(
//                new MyServlet(), new String[0]);
//        servletRegistrationBean.setLoadOnStartup(-10000);
//        return servletRegistrationBean;
//    }
}

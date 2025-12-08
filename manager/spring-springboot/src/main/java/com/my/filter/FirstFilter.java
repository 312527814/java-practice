package com.my.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

//@WebFilter(filterName="FirstFilter", urlPatterns="/*")
public class FirstFilter implements Filter {
    public FirstFilter(){
        System.out.println("FirstFilter.............");
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        System.out.println("通过注解方式进入拦截器..............");
        chain.doFilter(request, response);
//        System.out.println("拦截器结束..............");
    }
}

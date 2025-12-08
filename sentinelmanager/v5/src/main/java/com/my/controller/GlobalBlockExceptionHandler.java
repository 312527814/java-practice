package com.my.controller;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@Component
//public class GlobalBlockExceptionHandler implements BlockExceptionHandler {
//
//
//
//    @Override
//    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception {
//        System.out.println(e);
//    }
//}

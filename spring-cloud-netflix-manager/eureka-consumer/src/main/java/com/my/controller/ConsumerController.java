package com.my.controller;


import com.my.openfeign.TestService;
import com.my.openfeign.TestService2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@Controller
public class ConsumerController implements ApplicationContextAware {

    private Logger logger = LoggerFactory.getLogger(ConsumerController.class);
    //    Logger logger = Logger.getLogger(this.getClass().getName());
    ApplicationContext applicationContext;
    @Resource
    private RestTemplate restTemplate;
    @Resource(name = "lbRestTemplate")
    private RestTemplate lbRestTemplate;

    @LoadBalanced
    @Autowired(required = false)
    private Test test;

    @Autowired
    private TestService testService;
    @Autowired
    private TestService2 testService2;

//    @Autowired
//    Tracing tracer;

    @GetMapping("consumer/get")
    public String get(HttpServletRequest request) {
        logger.info("begin consumer/get ..............");
        String host = "producer1";
        String forObject2 = "";// restTemplate.getForObject("http://localhost:8084/api/check/web", String.class);

        String forObject = lbRestTemplate.getForObject("http://" + host + "/api/check/web", String.class);

        logger.info("end consumer/get ..............");
        return forObject + "|" + forObject2;
    }

    @GetMapping("consumer/get2")
    public String get2() {
        logger.info("begin consumer/get ..............");
        String s = testService.checkWeb("123");
        if (s != "") {
            throw new RuntimeException(s);
        }

        logger.info("end consumer/get ..............");
        return s;
    }

    @GetMapping("consumer/get3")
    public String get3() {
        logger.info("begin consumer/get3 ..............");

        String traceId = MDC.get("traceId");
        String spanId = MDC.get("spanId");

        logger.info("traceId,spanId:" + traceId + "," + spanId);

        String host = "producer1";
        String forObject = lbRestTemplate.getForObject("http://" + host + "/api/check/web", String.class);
        String s = testService.post("123post");

        lbRestTemplate.getForObject("http://" + host + "/api/check/web", String.class);
        testService.post("123post");
        logger.info("end consumer/get3 ..............");
        return forObject + ":";
    }

    @GetMapping("consumer/get4")
    public String get4() {
        logger.info("begin consumer/get4 ..............");
        String traceId = MDC.get("traceId");
        String spanId = MDC.get("spanId");
        logger.info("traceId,spanId:" + traceId + "," + spanId);

        String host = "consumer1";
        String forObject = lbRestTemplate.getForObject("http://" + host + "/consumer/get5", String.class);

        logger.info("end consumer/get4 ..............");
        return forObject + ":";
    }

    @GetMapping("consumer/get5")
    public String get5() {
        logger.info("begin consumer/get5 ..............");
        String host = "producer1";
        String forObject = lbRestTemplate.getForObject("http://" + host + "/api/check/web", String.class);

        logger.info("end consumer/get5 ..............");
        return forObject + "|";
    }

    @GetMapping("consumer/get6")
    public String get6() {
        logger.info("begin consumer/get6 ..............");

        logger.info("end consumer/get6 ..............");
        return "get6|";
    }

    @PostMapping("consumer/post")
    public String post(@RequestBody String input,HttpServletRequest request,HttpServletResponse response) {





        logger.info("begin consumer/post ..............");
        try {
            String post = testService.checkWeb();
        } catch (Exception e) {

            logger.error(e.toString());

        }
        logger.info("end consumer/post ..............");
        return input;
    }
    @PostMapping("consumer/post_")
    public String post_(@RequestBody String input,HttpServletRequest request,HttpServletResponse response) {





        logger.info("begin consumer/post ..............");
        try {
            String post = testService2.checkWeb();
        } catch (Exception e) {

            logger.error(e.toString());

        }
        logger.info("end consumer/post ..............");
        return input;
    }

    @PostMapping("consumer/post2")
    public String post2(@RequestBody Test input) {
        logger.info("begin consumer/post2 ..............");

        logger.info("end consumer/post2 ..............");
        return input.toString();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


}

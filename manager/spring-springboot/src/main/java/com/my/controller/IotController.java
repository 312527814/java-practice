package com.my.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.*;

@Controller
@RestController
public class IotController {

    int i = 0;
    @Resource
    Environment environment;

    @PostMapping("iot/message")
    public String index(String message) {


        Person person = new Person();
        System.out.println("load:"+person.getClass().getClassLoader());
        System.out.println("currentTheard=>" + Thread.currentThread().getName());
        String port = environment.getProperty("server.port");
        return "success";
    }

    @GetMapping("iot/index")
    public String index2(String message) {

        ss();
        return "success";
    }
    @PostMapping("videoAnalysis/receiveHaiKangLiGangVideo")
    public String index3( HttpServletRequest request) {

        System.out.println(".............."+new Date());

        ss(request);

//        System.out.println(user);
        return "success";
    }

    public void ss(){
        System.out.println("Starting memory allocation...");


        int size = 1024 * 1024 * 1024; // 1 GB

        for (int i = 0; i <4 ; i++) {
            byte[] bigArray = new byte[size];

            Arrays.fill(bigArray, (byte) 1);
            System.out.println("Allocated " + size / (1024 * 1024) + " MB of memory.");
        }



        System.out.println("Memory is allocated. Keeping it in use for 10 minutes...");

        try {

            Thread.sleep(10 * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Done.");
    }

    private void ss(HttpServletRequest request){
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()){
            String parameterName = parameterNames.nextElement();
            String value = request.getParameter(parameterName);
            System.out.println(value);
        }

    }

    private String getParameterFromRequest(HttpServletRequest request, String parameterName) {
        // 1. 首先尝试从URL参数中获取(GET请求)
        String value = request.getParameter(parameterName);

        // 2. 如果GET请求中没有，尝试从POST表单参数中获取
        if (value == null && "POST".equalsIgnoreCase(request.getMethod())) {

            if (request.getContentType() != null
                    && request.getContentType().contains("application/json")) {
                // 这里可以使用请求包装器来多次读取请求体
                // 实际项目中可以使用更完善的JSON解析方式
                try {
                    // 读取请求体
                    StringBuilder sb = new StringBuilder();
                    BufferedReader reader = request.getReader();
                    char[] charBuffer = new char[1024];
                    int bytesRead;
                    while ((bytesRead = reader.read(charBuffer)) != -1) {
                        sb.append(charBuffer, 0, bytesRead);
                    }
                    String body = sb.toString();

                    // 使用JSON解析器提取unitid
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(body);
                    if (jsonNode.has(parameterName)) {
                        value = jsonNode.get(parameterName).asText();
                    }
                } catch (Exception e) {
                    // 处理异常
                    e.printStackTrace();
                }
            }
        }

        return value;
    }


}

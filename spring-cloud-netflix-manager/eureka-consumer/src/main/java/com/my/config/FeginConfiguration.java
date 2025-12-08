package com.my.config;

import com.my.log.LoggerInfo;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * @program:
 * @description:
 * @author: liang.liu
 * @create: 2021-10-09 20:20
 */
public class FeginConfiguration {
//    @Value("${spring.application.name}")
//    private String application;
//    @Autowired
//    private KafkaTemplate<String, Object> kafkaTemplate;

//    @Bean
//    public RequestInterceptor requestInterceptor() {
//        RequestInterceptor requestInterceptor = template -> {
//            //传递日志traceId
//
//            String method = template.method();
//            if (method.toLowerCase().equals("post")) {
//                handlerPostMethod(template);
//            } else {
//                handlerGetMethod(template);
//            }
//        };
//        return requestInterceptor;
//    }

//    private void handlerGetMethod(RequestTemplate template) {
//        LoggerInfo loggerInfo = new LoggerInfo();
//        String traceId = MDC.get("traceId");
//        String spanId = MDC.get("spanId");
//        String url = template.url();
//        String dateStr = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(new Date());
//
//        loggerInfo.setApplication(application);
//        loggerInfo.setHost("127.0.0.1");
//        loggerInfo.setLevel("info");
//        loggerInfo.setPort(8080);
//        loggerInfo.setMessage("Get : " + url);
//        loggerInfo.setLine(0);
//        loggerInfo.setTheadName(Thread.currentThread().getName());
//        loggerInfo.setSpanceId(spanId);
//        loggerInfo.setTraceId(traceId);
//        loggerInfo.setDateTime(dateStr);
//        loggerInfo.setClassName(this.getClass().getName());
//        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send("my-log", traceId, loggerInfo);
//        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
//            @Override
//            public void onFailure(Throwable throwable) {
//                //发送失败的处理
////                System.out.println(" - 生产者 发送消息失败：" + throwable.getMessage());
//            }
//
//            @Override
//            public void onSuccess(SendResult<String, Object> stringObjectSendResult) {
//                //成功的处理
//
////                System.out.println(" - 生产者 发送消息成功：" + stringObjectSendResult.toString());
//            }
//        });
//    }

//    private void handlerPostMethod(RequestTemplate template) {
//        LoggerInfo loggerInfo = new LoggerInfo();
//
//        String traceId = MDC.get("traceId");
//        String spanId = MDC.get("spanId");
//        String url = template.url();
//        byte[] body = template.body();
//        String bodyStr = "";
//        if (body != null) {
//            bodyStr = new String(body);
//        }
//        String message = "Post : " + url + "\nbody\n" + bodyStr;
//        String dateStr = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(new Date());
//
//        loggerInfo.setApplication(application);
//        loggerInfo.setHost("127.0.0.1");
//        loggerInfo.setLevel("info");
//        loggerInfo.setPort(8080);
//        loggerInfo.setMessage(message);
//        loggerInfo.setLine(0);
//        loggerInfo.setTheadName(Thread.currentThread().getName());
//        loggerInfo.setSpanceId(spanId);
//        loggerInfo.setTraceId(traceId);
//        loggerInfo.setDateTime(dateStr);
//        loggerInfo.setClassName(this.getClass().getName());
//        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send("my-log", traceId, loggerInfo);
//        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
//            @Override
//            public void onFailure(Throwable throwable) {
//                //发送失败的处理
////                System.out.println(" - 生产者 发送消息失败：" + throwable.getMessage());
//            }
//
//            @Override
//            public void onSuccess(SendResult<String, Object> stringObjectSendResult) {
//                //成功的处理
//
////                System.out.println(" - 生产者 发送消息成功：" + stringObjectSendResult.toString());
//            }
//        });
//    }
}

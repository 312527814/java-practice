package com.my.config;

import com.my.log.LoggerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;

//@Component
public class LogTrackInterceptor implements ClientHttpRequestInterceptor {

    @Value("${spring.application.name}")
    private String application;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bodys, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {


        beforeHandler(httpRequest, bodys);

        ClientHttpResponse execute = clientHttpRequestExecution.execute(httpRequest, bodys);

        RestTemplateClientHttpResponse clientHttpResponse = new RestTemplateClientHttpResponse();
        //从execute中获取到response流，赋值给重写的ClientHttpResponse，解决流只能读一次的问题
        InputStream responseStream = execute.getBody();
        clientHttpResponse.setHeaders(execute.getHeaders());
        clientHttpResponse.setRawStatusCode(execute.getRawStatusCode());
        clientHttpResponse.setStatusCode(execute.getStatusCode());
        clientHttpResponse.setStatusText(execute.getStatusText());

        byte[] streamBytes = getStreamBytes(responseStream);

        afterHandler(httpRequest, clientHttpResponse, streamBytes);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(streamBytes);
        clientHttpResponse.setBody(byteArrayInputStream);
        // 保证请求继续被执行
        return clientHttpResponse;
    }

    private static byte[] getStreamBytes(InputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bytes = null;
        try {
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            bytes = byteArrayOutputStream.toByteArray();
            inputStream.close();
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }


    private void beforeHandler(HttpRequest request, byte[] body) {

        LoggerInfo loggerInfo = new LoggerInfo();

        String traceId = MDC.get("traceId");
//        String spanId = MDC.get("spanId");
        String spanId = MDC.get("X-B3-ParentSpanId");
        String bodyStr = new String(body);
        String url = request.getURI().getPath();
        String dateStr = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(new Date());

        loggerInfo.setApplication(application);
        loggerInfo.setHost("127.0.0.1");
        loggerInfo.setLevel("info");
        loggerInfo.setPort(8080);
        loggerInfo.setMessage(request.getMethodValue() + " : " + url + "\nbody\n" + bodyStr);
        loggerInfo.setLine(0);
        loggerInfo.setTheadName(Thread.currentThread().getName());
        loggerInfo.setSpanceId(spanId);
        loggerInfo.setTraceId(traceId);
        loggerInfo.setDateTime(dateStr);
        loggerInfo.setClassName(this.getClass().getName());
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send("my-log", traceId, loggerInfo);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable throwable) {
                //发送失败的处理
//                System.out.println(" - 生产者 发送消息失败：" + throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, Object> stringObjectSendResult) {
                //成功的处理

//                System.out.println(" - 生产者 发送消息成功：" + stringObjectSendResult.toString());
            }
        });
    }

    private void afterHandler(HttpRequest request, ClientHttpResponse response, byte[] streamBytes) {
        LoggerInfo loggerInfo = new LoggerInfo();

        String traceId = MDC.get("traceId");
//        String spanId = MDC.get("spanId");
        String spanId = MDC.get("X-B3-ParentSpanId");
        String url = request.getURI().getPath();
        String bodyStr = new String(streamBytes);
        String message = url + "\nresponse\n" + bodyStr;
        String dateStr = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(new Date());

        loggerInfo.setApplication(application);
        loggerInfo.setHost("127.0.0.1");
        loggerInfo.setLevel("info");
        loggerInfo.setPort(8080);
        loggerInfo.setMessage(message);
        loggerInfo.setLine(0);
        loggerInfo.setTheadName(Thread.currentThread().getName());
        loggerInfo.setSpanceId(spanId);
        loggerInfo.setTraceId(traceId);
        loggerInfo.setDateTime(dateStr);
        loggerInfo.setClassName(this.getClass().getName());
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send("my-log", traceId, loggerInfo);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable throwable) {
                //发送失败的处理
//                System.out.println(" - 生产者 发送消息失败：" + throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, Object> stringObjectSendResult) {
                //成功的处理

//                System.out.println(" - 生产者 发送消息成功：" + stringObjectSendResult.toString());
            }
        });
    }
}

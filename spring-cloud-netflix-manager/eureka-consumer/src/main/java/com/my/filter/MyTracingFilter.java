package com.my.filter;


import com.my.log.LoggerInfo;
import org.apache.kafka.clients.producer.*;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * @program:
 * @description:
 * @author: liang.liu
 * @create: 2021-10-12 16:03
 */
//@Component
public class MyTracingFilter implements Filter {
    @Value("${spring.application.name}")
    private String application;
//    @Autowired
//    Tracing tracing;
//    @Autowired
//    SpanNamer spanNamer;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;



        HttpServletResponse response = (HttpServletResponse) servletResponse;
        byte[] body = new byte[0];
        if (request.getMethod().toLowerCase().equals("post")) {
            body = readAsBytes(request);
            //对request进行重新包装
            byte[] finalBody = body;
            request = new HttpServletRequestWrapper(request) {
                @Override
                public ServletInputStream getInputStream() throws IOException {
                    return new ServletInputStreamWrapper(finalBody);
                }

                @Override
                public int getContentLength() {
                    return finalBody.length;
                }

                @Override
                public long getContentLengthLong() {
                    return finalBody.length;
                }
            };
        }


        beforeHandler(request, body);
        // 将新request包装对象传入过滤器链中
        chain.doFilter(request, servletResponse);

    }

    public static byte[] readAsBytes(HttpServletRequest request) {


        int len = request.getContentLength();
        byte[] buffer = new byte[len];
        ServletInputStream in = null;

        try {
            in = request.getInputStream();
            in.read(buffer, 0, len);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return buffer;
    }

    private void beforeHandler(HttpServletRequest request, byte[] body) {

//        LoggerInfo loggerInfo = new LoggerInfo();
//        String traceId = tracing.currentTraceContext().get().traceIdString();
////        String traceId = MDC.get("traceId");
//        String spanId = MDC.get("spanId");
//        String bodyStr = new String(body);
//        String url = request.getRequestURI();
//        String dateStr = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(new Date());
//
//        loggerInfo.setApplication(application);
//        loggerInfo.setHost("127.0.0.1");
//        loggerInfo.setLevel("info");
//        loggerInfo.setPort(8080);
//        loggerInfo.setMessage(request.getMethod() + " : " + url + "\nbody\n" + bodyStr);
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
//            }
//
//            @Override
//            public void onSuccess(SendResult<String, Object> stringObjectSendResult) {
//                //成功的处理
//
//            }
//        });
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
            }

            @Override
            public void onSuccess(SendResult<String, Object> stringObjectSendResult) {
                //成功的处理
            }
        });
    }


    public void test(HttpServletRequest request, byte[] body) {

        LoggerInfo loggerInfo = new LoggerInfo();

        String traceId = MDC.get("traceId");
        String spanId = MDC.get("spanId");
        String bodyStr = new String(body);
        String url = request.getRequestURI();
        String dateStr = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(new Date());

        loggerInfo.setApplication(application);
        loggerInfo.setHost("127.0.0.1");
        loggerInfo.setLevel("info");
        loggerInfo.setPort(8080);
        loggerInfo.setMessage(request.getMethod() + " : " + url + "\nbody\n" + bodyStr);
        loggerInfo.setLine(0);
        loggerInfo.setTheadName(Thread.currentThread().getName());
        loggerInfo.setSpanceId(spanId);
        loggerInfo.setTraceId(traceId);
        loggerInfo.setDateTime(dateStr);
        loggerInfo.setClassName(this.getClass().getName());

        Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.77.135:9092");
        props.put("acks", "-1");
//        props.put("retries", 0);
        props.put("batch.size", 1638400);
        props.put("linger.ms", 1000 * 1);
//        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");
//        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.springframework.kafka.support.serializer.JsonSerializer");


        Producer<String, LoggerInfo> producer = new KafkaProducer<>(props);

        ProducerRecord<String, LoggerInfo> record = new ProducerRecord<>("my-log", traceId, loggerInfo);

        Future<RecordMetadata> test1 = producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                long offset = metadata.offset();
                int partition = metadata.partition();
                String topic = metadata.topic();

                System.out.println("topic:= " + topic + " partition:= " + partition + " offset:= " + offset);
            }
        });

    }

    class ServletInputStreamWrapper extends ServletInputStream {
        private byte[] data;
        private int idx = 0;

        public ServletInputStreamWrapper(byte[] data) {
            if (data == null) {
                data = new byte[0];
            }

            this.data = data;
        }

        public int read() throws IOException {
            return this.idx == this.data.length ? -1 : this.data[this.idx++] & 255;
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener readListener) {

        }
    }
}

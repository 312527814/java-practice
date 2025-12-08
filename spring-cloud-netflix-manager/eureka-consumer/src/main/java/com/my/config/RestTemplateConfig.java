package com.my.config;

import com.my.controller.Test;
import com.my.log.LoggerInfo;
import feign.RequestTemplate;
import org.slf4j.MDC;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration;
import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer;
import org.springframework.cloud.client.loadbalancer.RetryLoadBalancerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configuration
public class RestTemplateConfig {



    @LoadBalanced
    @Bean
    public RestTemplate lbRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    @LoadBalanced
    @Bean
    public Test test() {
        return new Test();
    }

//    @Bean
//    public SmartInitializingSingleton loggerRestTemplateInitializerDeprecated(
//            final List<RestTemplate> restTemplates,LogTrackInterceptor logTrackInterceptor) {
//        return () -> {
//            if (restTemplates != null) {
//                restTemplates.forEach(f -> {
//                    List<ClientHttpRequestInterceptor> interceptors = f.getInterceptors();
//                    interceptors.add(logTrackInterceptor);
//                });
//            }
//        };
//    }
}

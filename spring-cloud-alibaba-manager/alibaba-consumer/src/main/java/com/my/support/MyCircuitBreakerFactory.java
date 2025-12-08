package com.my.support;

import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.ConfigBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.function.Function;

@Component
public class MyCircuitBreakerFactory extends CircuitBreakerFactory{
    @Override
    public CircuitBreaker create(String id) {
        return new MyCircuitBreaker();
    }

    @Override
    protected ConfigBuilder configBuilder(String id) {
        return new ConfigBuilder() {
            @Override
            public Object build() {
                return null;
            }
        };
    }

    @Override
    public void configureDefault(Function defaultConfiguration) {

    }
}


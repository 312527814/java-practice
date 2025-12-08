package com.my.support;

import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;

import java.util.function.Function;
import java.util.function.Supplier;

public class MyCircuitBreaker implements CircuitBreaker {


    @Override
    public <T> T run(Supplier<T> toRun, Function<Throwable, T> fallback) {
        try {
            return toRun.get();
        } catch (Exception ex) {
            return fallback.apply(ex);
        }

    }
}

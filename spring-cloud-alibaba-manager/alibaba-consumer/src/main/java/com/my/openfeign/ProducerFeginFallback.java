package com.my.openfeign;

import org.springframework.stereotype.Component;

@Component
public class ProducerFeginFallback implements ProducerFegin {
    @Override
    public String checkWeb() {
        return "fallback";
    }
}

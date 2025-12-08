package com.my.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "alibaba-producer",fallback = ProducerFeginFallback.class)
public interface ProducerFegin {
    @RequestMapping(method = RequestMethod.GET, value = "/test/get")
    String checkWeb();
}

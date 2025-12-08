package com.my.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.my.config.FeginConfiguration;

//@FeignClient(value = "producer",url = "localhost:8084")
@FeignClient(value = "${producer1}", configuration = FeginConfiguration.class,url = "${producer1-url}")
public interface TestService {
    @RequestMapping(method = RequestMethod.GET, value = "/api/check/web")
    String checkWeb();

    @RequestMapping(method = RequestMethod.GET, value = "/api/check/web2")
    String checkWeb(@RequestParam("id") String id);

    @RequestMapping(method = RequestMethod.POST, value = "/api/check/post")
    String post(@RequestBody String input);

    @RequestMapping(method = RequestMethod.POST, value = "/api/check/web")
    String post();

}

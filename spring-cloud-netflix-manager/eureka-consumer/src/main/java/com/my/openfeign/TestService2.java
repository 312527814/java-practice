package com.my.openfeign;

import com.my.config.FeginConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

//@FeignClient(value = "producer",url = "localhost:8084")
@FeignClient(value = "${producer2}")
public interface TestService2 {
    @RequestMapping(method = RequestMethod.GET, value = "/api/check/web")
    String checkWeb();

    @RequestMapping(method = RequestMethod.GET, value = "/api/check/web2")
    String checkWeb(@RequestParam("id") String id);

    @RequestMapping(method = RequestMethod.POST, value = "/api/check/post")
    String post(@RequestBody String input);

    @RequestMapping(method = RequestMethod.POST, value = "/api/check/web")
    String post();

}

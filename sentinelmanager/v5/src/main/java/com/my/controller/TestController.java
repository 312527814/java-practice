package com.my.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.web.bind.annotation.*;


@RestController
public class TestController {


    @GetMapping("/check/web4")
    public String alive() {
        return "successweb4";
    }

    @GetMapping("/check2/web4")
    public String alive2() {
        return "alive2successweb4";
    }

    @GetMapping("/check3/web4")
    public String alive3(@RequestParam("id") int id) {
        if (id == 2) {
            int a = 1 / 0;
        }
        return id + "";
    }

    @SentinelResource("HelloConsumer-hotKey5")
    @GetMapping("/check3/web5")
    public String alive3(@RequestParam("id") int id,@RequestParam("name")int name) {
        if (id == 2) {
            int a = 1 / 0;
        }
        return id + "";
    }

    @SentinelResource("HelloConsumer-hotKey")
    @GetMapping("/check3/web6/{orderId}/{id}")
    public String alive3(@PathVariable("orderId") Long orderId,@PathVariable("id") Long id) {
        return id + ""+orderId;
    }


    @SentinelResource("HelloConsumer-hotKey6")
    @PostMapping("/check3/web6")
    public String alive6(@RequestParam("id") int id,@RequestParam("name")int name) {
        if (id == 2) {
            int a = 1 / 0;
        }
        return id + "";
    }


}

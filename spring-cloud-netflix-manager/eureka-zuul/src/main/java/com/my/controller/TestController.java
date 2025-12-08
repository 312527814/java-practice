package com.my.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

@RestController
@Controller
public class TestController {
    Logger logger = Logger.getLogger(this.getClass().getName());

    @GetMapping("check/web")
    public String alive(HttpServletRequest request) {

//        logger.info("begin  check/web  ................");
        System.out.println("hello wold....");
        logger.info("end  check/web  ................");
        return "hello wold";
    }
}

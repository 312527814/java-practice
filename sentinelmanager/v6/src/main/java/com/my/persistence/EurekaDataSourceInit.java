package com.my.persistence;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.eureka.EurekaDataSource;
import com.alibaba.csp.sentinel.init.InitFunc;

import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;


public class EurekaDataSourceInit implements InitFunc {

    @Override
    public void init() {

        List<String> serviceUrls = new ArrayList<>();
        serviceUrls.add("http://localhost:8761/eureka");

        EurekaDataSource<List<FlowRule>> eurekaDataSource = new EurekaDataSource("sentinel-app",
                "eurekaInstanceConfig.getInstanceId()", serviceUrls, "flowrules", new Converter<String, List<FlowRule>>() {
            @Override
            public List<FlowRule> convert(String o) {
                return JSON.parseObject(o, new TypeReference<List<FlowRule>>() {
                });
            }
        });

        FlowRuleManager.register2Property(eurekaDataSource.getProperty());



    }
    private <T> String encodeJson(T t) {
        return JSON.toJSONString(t);
    }


}
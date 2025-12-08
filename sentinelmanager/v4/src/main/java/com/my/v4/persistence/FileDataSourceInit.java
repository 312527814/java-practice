package com.my.v4.persistence;

import com.alibaba.csp.sentinel.datasource.FileRefreshableDataSource;
import com.alibaba.csp.sentinel.datasource.FileWritableDataSource;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.WritableDataSource;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.transport.util.WritableDataSourceRegistry;
import com.alibaba.fastjson.TypeReference;

import com.alibaba.fastjson.JSON;

import java.util.List;

public class FileDataSourceInit implements InitFunc {

    @Override
    public void init() throws Exception {
        String flowRulePath = "F:/rule.txt";

        ReadableDataSource<String, List<FlowRule>> ds = new FileRefreshableDataSource<>(
                flowRulePath, source -> {
            List<FlowRule> list = JSON.parseArray(source, FlowRule.class);
            return list;
        });
        // 将可读数据源注册至 FlowRuleManager.
        FlowRuleManager.register2Property(ds.getProperty());

        WritableDataSource<List<FlowRule>> wds = new FileWritableDataSource<>(flowRulePath, this::encodeJson);
        // 将可写数据源注册至 transport 模块的 WritableDataSourceRegistry 中.
        // 这样收到控制台推送的规则时，Sentinel 会先更新到内存，然后将规则写入到文件中.
        WritableDataSourceRegistry.registerFlowDataSource(wds);





        String degradeRulePath = "F:/javalianxi/sentinelmanager/v4/degradeRule.txt";
        ReadableDataSource<String, List<DegradeRule>> degrade = new FileRefreshableDataSource<>(degradeRulePath, source -> JSON.parseObject(source, new TypeReference<List<DegradeRule>>() {
        }));

        DegradeRuleManager.register2Property(degrade.getProperty());
        WritableDataSource<List<DegradeRule>> degradewds = new FileWritableDataSource<>(degradeRulePath, this::encodeJson);
        WritableDataSourceRegistry.registerDegradeDataSource(degradewds);
    }

    private <T> String encodeJson(T t) {
        return JSON.toJSONString(t);
    }
}
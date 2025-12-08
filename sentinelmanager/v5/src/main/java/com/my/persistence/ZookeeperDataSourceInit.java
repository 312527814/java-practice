package com.my.persistence;

import com.alibaba.csp.sentinel.command.handler.ModifyParamFlowRulesCommandHandler;
import com.alibaba.csp.sentinel.datasource.FileRefreshableDataSource;
import com.alibaba.csp.sentinel.datasource.FileWritableDataSource;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.WritableDataSource;
import com.alibaba.csp.sentinel.datasource.zookeeper.ZookeeperDataSource;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.alibaba.csp.sentinel.transport.util.WritableDataSourceRegistry;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.List;

public class ZookeeperDataSourceInit implements InitFunc {
    public static final String remoteAddress="192.168.16.129:2181";

    @Override
    public void init() throws Exception {

        {
            final String path = "/sentinel/flow-rule";
            ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new ZookeeperDataSource<>(remoteAddress, path,
                    source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
                    }));
            ;
            FlowRuleManager.register2Property(flowRuleDataSource.getProperty());


            WritableDataSource<List<FlowRule>> wds = new ZookeeperWritableDataSource(path);
            // 将可写数据源注册至 transport 模块的 WritableDataSourceRegistry 中.
            // 这样收到控制台推送的规则时，Sentinel 会先更新到内存，然后将规则写入到zookeeper中.
            WritableDataSourceRegistry.registerFlowDataSource(wds);

        }
        {
            final String path = "/sentinel/degrade-rule";
            ReadableDataSource<String, List<DegradeRule>> DegradeDataSource = new ZookeeperDataSource<>(remoteAddress, path,
                    source -> JSON.parseObject(source, new TypeReference<List<DegradeRule>>() {
                    }));
            ;

            DegradeRuleManager.register2Property(DegradeDataSource.getProperty());


            WritableDataSource<List<DegradeRule>> degradewds = new ZookeeperWritableDataSource(path);


            WritableDataSourceRegistry.registerDegradeDataSource(degradewds);
        }
        {
            final String path = "/sentinel/paramFlow-rule";
            ReadableDataSource<String, List<ParamFlowRule>> paramFlowDataSource = new ZookeeperDataSource<>(remoteAddress, path,
                    source -> JSON.parseObject(source, new TypeReference<List<ParamFlowRule>>() {
                    }));
            ;

            ParamFlowRuleManager.register2Property(paramFlowDataSource.getProperty());


            WritableDataSource<List<ParamFlowRule>> paramFlowWds = new ZookeeperWritableDataSource(path);
            ModifyParamFlowRulesCommandHandler.setWritableDataSource(paramFlowWds);
        }

    }

    private <T> String encodeJson(T t) {
        return JSON.toJSONString(t);
    }
}
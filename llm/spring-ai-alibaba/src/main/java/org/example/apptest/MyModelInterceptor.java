package org.example.apptest;

import com.alibaba.cloud.ai.graph.agent.interceptor.ModelCallHandler;
import com.alibaba.cloud.ai.graph.agent.interceptor.ModelInterceptor;
import com.alibaba.cloud.ai.graph.agent.interceptor.ModelRequest;
import com.alibaba.cloud.ai.graph.agent.interceptor.ModelResponse;

public class MyModelInterceptor extends ModelInterceptor {

    @Override
    public String getName() {
        return "MyModelInterceptor";
    }

    @Override
    public ModelResponse interceptModel(ModelRequest request, ModelCallHandler handler) {
        System.out.println("Intercepting model call: ");
        return handler.call(request);
    }
}

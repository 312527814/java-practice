package org.example.apptest;

import com.alibaba.cloud.ai.graph.agent.interceptor.ToolCallHandler;
import com.alibaba.cloud.ai.graph.agent.interceptor.ToolCallRequest;
import com.alibaba.cloud.ai.graph.agent.interceptor.ToolCallResponse;
import com.alibaba.cloud.ai.graph.agent.interceptor.ToolInterceptor;

public class MyToolInterceptor extends ToolInterceptor {

    @Override
    public String getName() {
        return "MyToolInterceptor";
    }

    @Override
    public ToolCallResponse interceptToolCall(ToolCallRequest request, ToolCallHandler handler) {
        System.out.println("Intercepting tool call: " + request.getToolName());
        return handler.call(request);
    }
}

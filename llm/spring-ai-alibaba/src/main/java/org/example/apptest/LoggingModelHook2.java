package org.example.apptest;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.hook.HookPosition;
import com.alibaba.cloud.ai.graph.agent.hook.ModelHook;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class LoggingModelHook2 extends ModelHook {
    @Override
    public String getName() {
        return "logging_model_hook2";
    }

    @Override
    public HookPosition[] getHookPositions() {
        return new HookPosition[] {HookPosition.BEFORE_MODEL, HookPosition.AFTER_MODEL};
    }

    @Override
    public CompletableFuture<Map<String, Object>> beforeModel(OverAllState state, RunnableConfig config) {
        System.out.println("Before LoggingModelHook2 call");


        return CompletableFuture.completedFuture(Map.of());
    }

    @Override
    public CompletableFuture<Map<String, Object>> afterModel(OverAllState state, RunnableConfig config) {
        System.out.println("After LoggingModelHook2 call");
        return CompletableFuture.completedFuture(Map.of());
    }

    @Override
    public int getOrder() {
        return -100;
    }
}

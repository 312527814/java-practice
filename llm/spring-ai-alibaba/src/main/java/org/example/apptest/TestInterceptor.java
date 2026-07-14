package org.example.apptest;

import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.hook.ModelHook;
import com.alibaba.cloud.ai.graph.agent.hook.modelcalllimit.ModelCallLimitHook;
import com.alibaba.cloud.ai.graph.agent.interceptor.contextediting.ContextEditingInterceptor;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;

public class TestInterceptor {
    public static void main(String[] args) throws GraphRunnerException {
        // 初始化 ChatModel
        DeepSeekApi deepSeekApi = DeepSeekApi.builder()
                .apiKey(System.getenv("DEEPSEEK_API_KEY"))
                .build();

        ChatModel chatModel = DeepSeekChatModel.builder()
                .deepSeekApi(deepSeekApi)
                .defaultOptions(DeepSeekChatOptions.builder()
                        .model("deepseek-v4-pro")
                        .temperature(0.7)
                        .build())
                .build();

        // 定义天气查询工具
        ToolCallback weatherTool = FunctionToolCallback.builder("get_weather", new WeatherTool())
                .description("Get weather for a given city")
                .inputType(WeatherRequest.class)
                .build();
        ModelHook loggingHook = new LoggingModelHook();
        // 使用
        ReactAgent agent = ReactAgent.builder()
                .name("resilient_agent")
                .model(chatModel)
                .tools(weatherTool)
                .hooks(loggingHook,new LoggingModelHook2())
//                .interceptors(new MyModelInterceptor(),new MyToolInterceptor())
                .build();

        AssistantMessage response = agent.call("what is the weather in San Francisco");
        System.out.println(response.getText());
    }
}

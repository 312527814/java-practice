package org.example.apptest;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.hook.ModelHook;
import com.alibaba.cloud.ai.graph.agent.hook.modelcalllimit.ModelCallLimitHook;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import org.example.hooks.RAGMessagesHook;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.content.Media;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.MimeTypeUtils;

public class TestHook {
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
        String customSchema = "请按照以下JSON格式输出：\n"
                + "{\n"
                + "    \"title\": \"标题\",\n"
                + "    \"content\": \"内容\",\n"
                + "    \"style\": \"风格\"\n"
                + "}";

        ModelHook loggingHook = new LoggingModelHook();

        ModelCallLimitHook hook = ModelCallLimitHook.builder()
                .runLimit(5)  // 限制最多调用 5 次
                .exitBehavior(ModelCallLimitHook.ExitBehavior.ERROR)  // 超出限制时抛出异常
                .build();

        // threadId 是给定对话的唯一标识符
        RunnableConfig runnableConfig = RunnableConfig.builder().threadId("threadId").addMetadata("user_id", "1").build();
        runnableConfig.context().put("1","222");
        // 创建 agent
        ReactAgent agent = ReactAgent.builder()
                .name("weather_agent")
                .model(chatModel)
                .tools(weatherTool)
                .hooks(loggingHook)
                .systemPrompt("You are a helpful assistant")
                .saver(new MemorySaver())
                .outputSchema(customSchema)
                .build();
        AssistantMessage response=null;
//      response = agent.call("你是什么模型？",runnableConfig);
        response = agent.call("what is the weather in San Francisco?", runnableConfig);
        System.out.println(response.getText());
    }
}

package org.example.apptest;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.content.Media;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.MimeTypeUtils;

public class TestAgent {
    public static void main(String[] args) throws GraphRunnerException {
// 初始化 ChatModel
        DashScopeApi dashScopeApi = DashScopeApi.builder()
                .apiKey(System.getenv("ALI_AI_KEY"))
                .build();

        ChatModel chatModel = DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .defaultOptions(DashScopeChatOptions.builder()
                        .withModel("qwen3.7-plus")      // 换成你要的模型
                        .withTemperature(0.7)        // 可选
                        .withMaxToken(2000)          // 可选
                        .withMultiModel(true) .build())
                .build();

        // 定义天气查询工具
        ToolCallback weatherTool = FunctionToolCallback.builder("get_weather", new WeatherTool())
                .description("Get weather for a given city")
                .inputType(WeatherRequest.class)
                .build();

        // 创建 agent
        ReactAgent agent = ReactAgent.builder()
                .name("weather_agent")
                .model(chatModel)
                .tools(weatherTool)
                .systemPrompt("You are a helpful assistant")
                .saver(new MemorySaver())
                .build();

        UserMessage userMessage = UserMessage.builder()
                .text("描述一下这个图片。从长相来看，100分的话 能给打多少分？")
                .media(new Media(MimeTypeUtils.IMAGE_PNG, new FileSystemResource("F:/tmp/test.jpg")))
                .build();
// 运行 agent
        AssistantMessage response = agent.call(userMessage);
        System.out.println(response.getText());
    }
}

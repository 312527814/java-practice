package org.example.apptest;

import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.action.InterruptionMetadata;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.hook.hip.HumanInTheLoopHook;
import com.alibaba.cloud.ai.graph.agent.hook.hip.ToolConfig;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class TestHumanInTheLoop {
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

        ToolCallback weatherTool = FunctionToolCallback.builder("get_weather", new WeatherTool())
                .description("Get weather for a given city")
                .inputType(WeatherRequest.class)
                .build();

        MemorySaver memorySaver = new MemorySaver();

        HumanInTheLoopHook humanInTheLoopHook = HumanInTheLoopHook.builder()
                .approvalOn("get_weather", ToolConfig.builder()
                        .description("查询天气需要审批")
                        .build())
                .build();

        ReactAgent agent = ReactAgent.builder()
                .name("approval_agent")
                .model(chatModel)
                .tools(weatherTool)
                .hooks(List.of(humanInTheLoopHook))
                .interceptors(new MyModelInterceptor())
                .saver(memorySaver)
                .enableLogging(true)
                .build();

        // HITL 必须指定 threadId，用于 checkpoint 恢复
        String threadId = "hitl-test-thread";
        RunnableConfig config = RunnableConfig.builder()
                .threadId(threadId)
                .build();

        // 第一次调用：触发工具审批中断（不能用 call()，要用 invokeAndGetOutput 检测中断）
        System.out.println("=== 第一次调用：期望触发人工审批中断 ===");
        Optional<NodeOutput> result = agent.invokeAndGetOutput(
                "what is the weather in San Francisco",
                config);

        if (result.isEmpty()) {
            System.out.println("第一次调用无返回，请检查 DEEPSEEK_API_KEY 或模型是否调用了 get_weather");
            return;
        }

        if (result.get() instanceof InterruptionMetadata interruption) {
            System.out.println("检测到人工审批中断，待审批工具：");
            interruption.toolFeedbacks().forEach(feedback -> {
                System.out.println("  工具: " + feedback.getName());
                System.out.println("  参数: " + feedback.getArguments());
                System.out.println("  说明: " + feedback.getDescription());
            });
            // 构建批准反馈
            InterruptionMetadata.Builder feedbackBuilder = InterruptionMetadata.builder()
                    .nodeId(interruption.node())
                    .state(interruption.state());
            System.out.print("是否调用该工具？(yes/no): ");
            System.out.flush();
            Scanner scanner = new Scanner(System.in);
            String name = scanner.nextLine().trim();

            if(name.equals("yes")){
                interruption.toolFeedbacks().forEach(toolFeedback ->
                        feedbackBuilder.addToolFeedback(
                                InterruptionMetadata.ToolFeedback.builder(toolFeedback)
                                        .result(InterruptionMetadata.ToolFeedback.FeedbackResult.APPROVED)
                                        .build()));
                // 第二次调用：resumeConfig + 空消息恢复执行
                System.out.println("\n=== 第二次调用：批准并恢复执行 ===");
            }else {
                interruption.toolFeedbacks().forEach(toolFeedback ->
                        feedbackBuilder.addToolFeedback(
                                InterruptionMetadata.ToolFeedback.builder(toolFeedback)
                                        .result(InterruptionMetadata.ToolFeedback.FeedbackResult.REJECTED)
                                        .build()));
                // 第二次调用：resumeConfig + 空消息恢复执行
                System.out.println("\n=== 第二次调用：拒绝执行 ===");
            }


            InterruptionMetadata approvalMetadata = feedbackBuilder.build();
            RunnableConfig resumeConfig = RunnableConfig.builder()
                    .threadId(threadId)
                    .addMetadata(RunnableConfig.HUMAN_FEEDBACK_METADATA_KEY, approvalMetadata)
                    .build();



            AssistantMessage response = agent.call("", resumeConfig);
            System.out.println("最终结果: " + response.getText());
            return;
        }
        System.out.println("未触发人工审批，首次返回类型: " + result.get().getClass().getSimpleName());
    }
}

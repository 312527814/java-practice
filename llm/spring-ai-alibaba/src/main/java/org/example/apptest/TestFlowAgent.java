package org.example.apptest;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.flow.agent.LlmRoutingAgent;
import com.alibaba.cloud.ai.graph.agent.flow.agent.ParallelAgent;
import com.alibaba.cloud.ai.graph.agent.flow.agent.SequentialAgent;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * FlowAgent 示例：顺序 / 并行 / 路由。
 * <p>
 * 运行参数（可选）：
 * <ul>
 *   <li>无参数或 all：依次跑三种模式</li>
 *   <li>sequential / parallel / routing：只跑指定模式</li>
 * </ul>
 * 参考官网：
 * https://java2ai.com/docs/frameworks/agent-framework/advanced/multi-agent
 */
public class TestFlowAgent {

    public static void main(String[] args) throws Exception {
        ChatModel chatModel = buildChatModel();
        String mode = args.length > 0 ? args[0].trim().toLowerCase() : "all";

        switch (mode) {
            case "sequential" -> runSequential(chatModel);
            case "parallel" -> runParallel(chatModel);
            case "routing" -> runRouting(chatModel);
            case "all" -> {
                runSequential(chatModel);
                System.out.println();
                runParallel(chatModel);
                System.out.println();
                runRouting(chatModel);
            }
            default -> {
                System.err.println("未知模式: " + mode);
                System.err.println("可用: all | sequential | parallel | routing");
            }
        }
    }

    /**
     * 顺序：writer → reviewer
     */
    private static void runSequential(ChatModel chatModel) throws Exception {
        System.out.println("========== SequentialAgent（顺序） ==========");

        ReactAgent writerAgent = ReactAgent.builder()
                .name("writer_agent")
                .model(chatModel)
                .description("专业写作Agent")
                .instruction("你是一个知名的作家，擅长写作和创作。请根据用户的提问进行回答：{input}。")
                .outputKey("article")
                .build();

        ReactAgent reviewerAgent = ReactAgent.builder()
                .name("reviewer_agent")
                .model(chatModel)
                .description("专业评审Agent")
                .instruction("你是一个知名的评论家，擅长对文章进行评论和修改。"
                        + "对于散文类文章，请确保文章中必须包含对于西湖风景的描述。待评论文章：\n\n{article}"
                        + "最终只返回修改后的文章，不要包含任何评论信息。")
                .outputKey("reviewed_article")
                .build();

        SequentialAgent blogAgent = SequentialAgent.builder()
                .name("blog_agent")
                .description("根据用户给定的主题写一篇文章，然后将文章交给评论员进行评论")
                .subAgents(List.of(writerAgent, reviewerAgent))
                .build();

        Optional<OverAllState> result = blogAgent.invoke("帮我写一个100字左右的散文，主题是春天");
        result.ifPresent(state -> {
            printStateValue(state, "article", "原始文章");
            printStateValue(state, "reviewed_article", "评审后文章");
        });
    }

    /**
     * 并行：散文 / 现代诗 / 总结 同时执行后合并
     */
    private static void runParallel(ChatModel chatModel) throws Exception {
        System.out.println("========== ParallelAgent（并行） ==========");

        ReactAgent proseWriterAgent = ReactAgent.builder()
                .name("prose_writer_agent")
                .model(chatModel)
                .description("专门写散文的AI助手")
                .instruction("你是一个知名的散文作家，擅长写优美的散文。"
                        + "用户会给你一个主题：{input}，你只需要创作一篇100字左右的散文。")
                .outputKey("prose_result")
                .enableLogging(true)
                .build();

        ReactAgent poemWriterAgent = ReactAgent.builder()
                .name("poem_writer_agent")
                .model(chatModel)
                .description("专门写现代诗的AI助手")
                .instruction("你是一个知名的现代诗人，擅长写现代诗。"
                        + "用户会给你的主题是：{input}，你只需要创作一首现代诗。")
                .outputKey("poem_result")
                .enableLogging(true)
                .build();

        ReactAgent summaryAgent = ReactAgent.builder()
                .name("summary_agent")
                .model(chatModel)
                .description("专门做内容总结的AI助手")
                .instruction("你是一个专业的内容分析师，擅长对主题进行总结和提炼。"
                        + "用户会给你一个主题：{input}，你只需要对这个主题进行简要总结。")
                .outputKey("summary_result")
                .enableLogging(true)
                .build();

        String parallelName = "parallel_creative_agent";
        ParallelAgent parallelAgent = ParallelAgent.builder()
                .name(parallelName)
                .description("并行执行多个创作任务，包括写散文、写诗和做总结")
                .mergeOutputKey("merged_results")
                .subAgents(List.of(proseWriterAgent, poemWriterAgent, summaryAgent))
                .mergeStrategy(new ParallelAgent.DefaultMergeStrategy())
                .build();

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        try {
            RunnableConfig config = RunnableConfig.builder()
                    .addParallelNodeExecutor(parallelName, executorService)
                    .build();

            AtomicReference<NodeOutput> lastOutput = new AtomicReference<>();
            parallelAgent.stream("以'西湖'为主题", config)
                    .doOnNext(nodeOutput -> {
                        System.out.println("节点输出: " + nodeOutput);
                        lastOutput.set(nodeOutput);
                    })
                    .doOnError(error -> System.err.println("并行执行出错: " + error.getMessage()))
                    .blockLast();

            NodeOutput output = lastOutput.get();
            if (output == null || output.state() == null) {
                System.out.println("未收到并行输出");
                return;
            }
            OverAllState state = output.state();
            printStateValue(state, "prose_result", "散文");
            printStateValue(state, "poem_result", "诗歌");
            printStateValue(state, "summary_result", "总结");
            printStateValue(state, "merged_results", "合并结果");
        } finally {
            executorService.shutdown();
        }
    }

    /**
     * 路由：LLM 按意图分发给写作 / 评审 / 翻译专家
     */
    private static void runRouting(ChatModel chatModel) throws Exception {
        System.out.println("========== LlmRoutingAgent（路由） ==========");

        ReactAgent writerAgent = ReactAgent.builder()
                .name("writer_agent")
                .model(chatModel)
                .description("擅长创作各类文章，包括散文、诗歌等文学作品")
                .instruction("你是一个知名的作家，擅长写作和创作。请根据用户的提问进行回答。")
                .outputKey("writer_output")
                .build();

        ReactAgent reviewerAgent = ReactAgent.builder()
                .name("reviewer_agent")
                .model(chatModel)
                .description("擅长对文章进行评论、修改和润色")
                .instruction("你是一个知名的评论家，擅长对文章进行评论和修改。"
                        + "对于散文类文章，请确保文章中必须包含对于西湖风景的描述。")
                .outputKey("reviewer_output")
                .build();

        ReactAgent translatorAgent = ReactAgent.builder()
                .name("translator_agent")
                .model(chatModel)
                .description("擅长将文章翻译成各种语言")
                .instruction("你是一个专业的翻译家，能够准确地将文章翻译成目标语言。")
                .outputKey("translator_output")
                .build();

        LlmRoutingAgent routingAgent = LlmRoutingAgent.builder()
                .name("content_routing_agent")
                .description("根据用户需求智能路由到合适的专家Agent")
                .model(chatModel)
                .subAgents(List.of(writerAgent, reviewerAgent, translatorAgent))
                .build();

        System.out.println("--- 路由测试1: 写作请求 ---");
        printRoutingResult(routingAgent.invoke("帮我写一篇关于春天的散文"),
                "writer_output", "reviewer_output", "translator_output");

        System.out.println("--- 路由测试2: 修改请求 ---");
        printRoutingResult(routingAgent.invoke("请帮我修改这篇文章：春天来了，花开了。"),
                "writer_output", "reviewer_output", "translator_output");

        System.out.println("--- 路由测试3: 翻译请求 ---");
        printRoutingResult(routingAgent.invoke("请将以下内容翻译成英文：春暖花开"),
                "writer_output", "reviewer_output", "translator_output");
    }

    private static ChatModel buildChatModel() {
        DashScopeApi dashScopeApi = DashScopeApi.builder()
                .apiKey(System.getenv("ALI_AI_KEY"))
                .build();

        return DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .defaultOptions(DashScopeChatOptions.builder()
                        .withModel("qwen3.7-plus")
                        .withTemperature(0.7)
                        .withMaxToken(2000)
                        .build())
                .build();
    }

    private static void printRoutingResult(Optional<OverAllState> result, String... keys) {
        if (result.isEmpty()) {
            System.out.println("无结果");
            return;
        }
        OverAllState state = result.get();
        for (String key : keys) {
            state.value(key).ifPresent(value -> printValue(key, value));
        }
    }

    private static void printStateValue(OverAllState state, String key, String label) {
        state.value(key).ifPresent(value -> printValue(label, value));
    }

    private static void printValue(String label, Object value) {
        if (value instanceof AssistantMessage message) {
            System.out.println(label + ": " + message.getText());
        } else {
            System.out.println(label + ": " + value);
        }
    }
}

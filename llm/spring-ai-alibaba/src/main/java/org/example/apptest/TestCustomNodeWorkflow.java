package org.example.apptest;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.graph.CompileConfig;
import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.NodeOutput;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncEdgeAction;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.alibaba.cloud.ai.graph.streaming.StreamingOutput;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Workflow 示例：自定义 NodeAction 节点 + 条件边。
 * <p>
 * 流程：START → preprocess → classify → (complaint/praise/other) → END
 * <p>
 * 与 {@link TestWorkflow} 的区别：这里节点是自己实现的类，而不是 ReactAgent.asNode(...)。
 */
public class TestCustomNodeWorkflow {

    public static void main(String[] args) throws Exception {
        DashScopeApi dashScopeApi = DashScopeApi.builder()
                .apiKey(System.getenv("ALI_AI_KEY"))
                .build();

        ChatModel chatModel = DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .defaultOptions(DashScopeChatOptions.builder()
                        .withModel("qwen3.7-plus")
                        .withTemperature(0.7)
                        .withMaxToken(2000)
                        .build())
                .build();

        ChatClient.Builder chatClientBuilder = ChatClient.builder(chatModel);

        KeyStrategyFactory keyStrategyFactory = () -> {
            HashMap<String, KeyStrategy> strategies = new HashMap<>();
            strategies.put("input", new ReplaceStrategy());
            strategies.put("cleaned_input", new ReplaceStrategy());
            strategies.put("category", new ReplaceStrategy());
            strategies.put("summary", new ReplaceStrategy());
            return strategies;
        };

        StateGraph workflow = new StateGraph(keyStrategyFactory);

        // 自定义 Node：用 node_async(...) 包装后挂到图上
        workflow.addNode("preprocess", AsyncNodeAction.node_async(new PreprocessorNode()));
        workflow.addNode("classify", AsyncNodeAction.node_async(new KeywordClassifyNode()));
        workflow.addNode("handle_complaint", AsyncNodeAction.node_async(new SummaryNode(
                chatClientBuilder,
                "你是客服质检员。用户反馈是投诉类，请用 2~3 句话总结问题并给出处理建议。原文：\n{text}")));
        workflow.addNode("handle_praise", AsyncNodeAction.node_async(new SummaryNode(
                chatClientBuilder,
                "你是品牌运营。用户反馈是好评类，请提炼亮点并生成一句可用于宣传的短文案。原文：\n{text}")));
        workflow.addNode("handle_other", AsyncNodeAction.node_async(new SummaryNode(
                chatClientBuilder,
                "你是内容助手。请对以下反馈做简短摘要：\n{text}")));

        workflow.addEdge(StateGraph.START, "preprocess");
        workflow.addEdge("preprocess", "classify");

        // 条件边：根据 classify 写入的 category 选择下游节点
        workflow.addConditionalEdges(
                "classify",
                AsyncEdgeAction.edge_async(state -> state.value("category", "other").toString()),
                Map.of(
                        "complaint", "handle_complaint",
                        "praise", "handle_praise",
                        "other", "handle_other"));

        workflow.addEdge("handle_complaint", StateGraph.END);
        workflow.addEdge("handle_praise", StateGraph.END);
        workflow.addEdge("handle_other", StateGraph.END);

        CompiledGraph compiledGraph = workflow.compile(CompileConfig.builder().build());

        Map<String, Object> input = Map.of(
                "input",
                "  物流太慢了，包裹拖了一周才到，客服也不回消息，非常不满意！！  ");

        System.out.println("=== 开始执行自定义 Node Workflow ===");
        NodeOutput lastOutput = compiledGraph.stream(input)
                .doOnNext(output -> {
                    if (output instanceof StreamingOutput<?> streamingOutput) {
                        System.out.println("节点 " + streamingOutput.node()
                                + " 状态: " + streamingOutput.state().data());
                    }
                })
                .blockLast();

        System.out.println("\n=== 最终状态 ===");
        if (lastOutput != null && lastOutput.state() != null) {
            OverAllState state = lastOutput.state();
            System.out.println("cleaned_input: " + state.value("cleaned_input").orElse("无"));
            System.out.println("category: " + state.value("category").orElse("无"));
            System.out.println("summary: " + state.value("summary").orElse("无"));
        }
    }

    /**
     * 预处理节点：清洗输入文本。
     */
    static class PreprocessorNode implements NodeAction {
        @Override
        public Map<String, Object> apply(OverAllState state) {
            String input = state.value("input", "").toString();
            String cleaned = input.trim().replaceAll("\\s+", " ");
            System.out.println("[preprocess] 清洗前长度=" + input.length() + ", 清洗后=" + cleaned);
            return Map.of("cleaned_input", cleaned);
        }
    }

    /**
     * 分类节点：按关键词决定路由类别（不调用大模型）。
     */
    static class KeywordClassifyNode implements NodeAction {
        @Override
        public Map<String, Object> apply(OverAllState state) {
            String text = state.value("cleaned_input", "").toString().toLowerCase();
            String category;
            if (containsAny(text, "投诉", "不满", "差", "慢", "退款", "破损")) {
                category = "complaint";
            } else if (containsAny(text, "好评", "满意", "喜欢", "赞", "推荐")) {
                category = "praise";
            } else {
                category = "other";
            }
            System.out.println("[classify] category=" + category);
            return Map.of("category", category);
        }

        private boolean containsAny(String text, String... keywords) {
            for (String keyword : keywords) {
                if (text.contains(keyword)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 摘要节点：调用 LLM，按不同提示词生成 summary。
     */
    static class SummaryNode implements NodeAction {
        private final ChatClient chatClient;
        private final String promptTemplate;

        SummaryNode(ChatClient.Builder chatClientBuilder, String promptTemplate) {
            this.chatClient = chatClientBuilder.build();
            this.promptTemplate = promptTemplate;
        }

        @Override
        public Map<String, Object> apply(OverAllState state) {
            String text = state.value("cleaned_input", "").toString();
            String prompt = promptTemplate.replace("{text}", text);
            String summary = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
            System.out.println("[summary] 生成完成, category="
                    + state.value("category").orElse("unknown"));
            return Map.of("summary", summary == null ? "" : summary);
        }
    }
}

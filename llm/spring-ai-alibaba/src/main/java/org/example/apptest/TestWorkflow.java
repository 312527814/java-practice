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
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.alibaba.cloud.ai.graph.streaming.StreamingOutput;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.cloud.ai.graph.action.AsyncNodeAction.node_async;

/**
 * Workflow 示例：将 ReactAgent 作为 StateGraph 的 SubGraph Node，
 * 并演示 ReplaceStrategy 对同一键二次写入时的覆盖效果。
 * <p>
 * 流程：START → data_analyzer → supplement → report_generator → END
 * <p>
 * 参考官网文档：
 * https://java2ai.com/docs/frameworks/agent-framework/advanced/workflow
 */
public class TestWorkflow {

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

        // 数据分析 Agent：读取 input，输出 analysis_result（第一次写入）
        ReactAgent analysisAgent = ReactAgent.builder()
                .name("data_analyzer")
                .model(chatModel)
                .instruction("你是一个数据分析专家，负责分析数据并提供洞察，请分析以下输入数据：\n{input}")
                .outputKey("analysis_result")
                .enableLogging(true)
                .build();

        // 报告生成 Agent：读取 analysis_result，输出 final_report
        ReactAgent reportAgent = ReactAgent.builder()
                .name("report_generator")
                .model(chatModel)
                .instruction("你是一个报告生成专家，负责将分析结果 “{analysis_result}” 转化为专业报告")
                .outputKey("final_report")
                .enableLogging(true)
                .build();

        KeyStrategyFactory keyStrategyFactory = () -> {
            HashMap<String, KeyStrategy> strategies = new HashMap<>();
            strategies.put("input", new ReplaceStrategy());
            // 同一键被 data_analyzer、supplement 各写一次 → Replace 会覆盖
            strategies.put("analysis_result", new ReplaceStrategy());
            strategies.put("final_report", new ReplaceStrategy());
            return strategies;
        };

        StateGraph workflow = new StateGraph(keyStrategyFactory);

        workflow.addNode(analysisAgent.name(), analysisAgent.asNode(true, false));
        // 自定义节点：再次写入 analysis_result，演示 ReplaceStrategy 覆盖
        workflow.addNode("supplement", node_async(new SupplementNode()));
        workflow.addNode(reportAgent.name(), reportAgent.asNode(true, false));

        workflow.addEdge(StateGraph.START, analysisAgent.name());
        workflow.addEdge(analysisAgent.name(), "supplement");
        workflow.addEdge("supplement", reportAgent.name());
        workflow.addEdge(reportAgent.name(), StateGraph.END);

        CompiledGraph compiledGraph = workflow.compile(CompileConfig.builder().build());

        Map<String, Object> input = Map.of(
                "input",
                "2025年全年销量100亿，毛利率 23%，净利率 13%。2024年全年销量80亿，毛利率 20%，净利率 8%。");

        System.out.println("=== 开始执行 Workflow（流式）===");
        NodeOutput lastOutput = compiledGraph.stream(input)
                .doOnNext(output -> {
                    if (output instanceof StreamingOutput<?> streamingOutput) {
                        if (streamingOutput.message() != null) {
                            System.out.println("Streaming from node " + streamingOutput.node()
                                    + ": " + streamingOutput.message().getText());
                        } else {
                            System.out.println("Output from node " + streamingOutput.node()
                                    + ": " + streamingOutput.state().data());
                        }
                    }
                })
                .blockLast();

        System.out.println("\n=== Workflow 最终状态 ===");
        if (lastOutput != null && lastOutput.state() != null) {
            System.out.println(lastOutput.state().data());
            System.out.println("\nanalysis_result（被 supplement 覆盖后）: "
                    + stringify(lastOutput.state().value("analysis_result").orElse("无")));
            System.out.println("\n最终报告: "
                    + stringify(lastOutput.state().value("final_report").orElse("无")));
        }
    }

    /**
     * 中间节点：读取上一步 analysis_result，再 return 同名键，触发 ReplaceStrategy 覆盖。
     */
    static class SupplementNode implements NodeAction {
        @Override
        public Map<String, Object> apply(OverAllState state) {
            Object previous = state.value("analysis_result").orElse("（空）");
            System.out.println("\n[supplement] 覆盖前 analysis_result = " + stringify(previous));

            String replacement = "补充：净利率也提升了（由 supplement 节点二次写入，ReplaceStrategy 覆盖上一步结果）";
            System.out.println("[supplement] 即将写入 analysis_result = " + replacement + "\n");

            // 关键：通过 return Map 写状态，不是 state.put
            return Map.of("analysis_result", replacement);
        }
    }

    private static String stringify(Object value) {
        if (value instanceof AssistantMessage message) {
            return message.getText();
        }
        return String.valueOf(value);
    }
}

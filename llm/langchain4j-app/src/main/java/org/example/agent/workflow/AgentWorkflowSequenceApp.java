package org.example.agent.workflow;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.UntypedAgent;
import dev.langchain4j.community.model.dashscope.QwenChatModel;

import java.util.HashMap;
import java.util.Map;

/*
Sequential workflow（顺序工作流）,是由多个纯代理Agent沟通。
 */
public class AgentWorkflowSequenceApp {
    public static void main(String[] args) {

        String getenv = System.getenv("ALI_AI_KEY");
        QwenChatModel chatModel = QwenChatModel.builder()
                .baseUrl("https://dashscope.aliyuncs.com/api/v1")
                .modelName("qwen3-vl-plus")
                .apiKey(getenv)
                .build();

        // 2. 构建各个智能体实例
        CreativeWriter writer = AgenticServices.agentBuilder(CreativeWriter.class)
                .chatModel(chatModel)
                .build();

        StyleEditor editor = AgenticServices.agentBuilder(StyleEditor.class)
                .chatModel(chatModel)
                .build();

        StyleScorer scorer = AgenticServices.agentBuilder(StyleScorer.class)
                .chatModel(chatModel)
                .build();

        // 3. 构建一个“编辑-评分”循环工作流（迭代优化直到分数达标）
        UntypedAgent reviewLoop = AgenticServices.loopBuilder()
                .subAgents(scorer, editor) // 先评分，再编辑
                .maxIterations(1) // 最多循环5次
                .exitCondition(agenticScope -> {
                    // 从共享作用域中读取上次评分的分数
                    double score = agenticScope.readState("score", 0.0);
                    System.out.println(score);
                    return score >=1; // 分数达到0.8则退出循环
                })
                .build();

        // 4. 构建一个总顺序工作流：先创作，再进入优化循环
        UntypedAgent storyProductionWorkflow = AgenticServices.sequenceBuilder()
                .subAgents(writer, reviewLoop)
                .outputKey("story") // 整个工作流的最终输出是“story”
                .build();

        // 5. 执行工作流
        Map<String, Object> initialInput = new HashMap<>();
        initialInput.put("topic", "龙与魔法师");
        initialInput.put("style", "奇幻");
        String finalStory = (String) storyProductionWorkflow.invoke(initialInput);
        System.out.println("最终故事：" + finalStory);
    }
}

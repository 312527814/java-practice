package org.example.agent.pureagent;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import org.example.agent.workflow.CreativeWriter;

import java.io.IOException;

//纯代理Agent
public class CreativeWriter_Agent_Example {

    public static void main(String[] args) throws IOException {

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
        String result = writer.writeStory("龙与魔法师");

        System.out.println(result);
    }
}
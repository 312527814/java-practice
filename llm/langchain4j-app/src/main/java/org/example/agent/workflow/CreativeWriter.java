package org.example.agent.workflow;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface CreativeWriter {
    @Agent(outputKey = "story", description = "根据主题创作故事")
    @UserMessage("请根据以下主题创作一个故事：{{topic}}，不要超过20个字,要再内容最后拼接一个随机数")  // 添加用户消息模板
    String writeStory(@V("topic") String topic);
}
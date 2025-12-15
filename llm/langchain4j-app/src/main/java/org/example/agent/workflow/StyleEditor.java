package org.example.agent.workflow;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface StyleEditor {
    @Agent(outputKey = "story", description = "根据指定风格编辑故事")
    @UserMessage("请将以下故事改写为{{style}}风格：\n{{story}},不要超过20个字,要再内容最后拼接一个随机数")
    String editStory(@V("story") String story, @V("style") String style);
}

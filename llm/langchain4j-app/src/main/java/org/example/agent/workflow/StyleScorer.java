package org.example.agent.workflow;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface StyleScorer {
    @Agent(outputKey = "score", description = "为故事的风格符合度打分 (0.0-1.0)")
    @UserMessage("""
            请为以下故事在{{style}}风格上的符合度打分（0.0-1.0）：
            故事：{{story}}
            目标风格：{{style}}
            
            只返回一个数字，例如：0.75
            """)
    double scoreStory(@V("story") String story, @V("style") String style);
}

package org.example.assistant;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT, chatModel = "qwenChatModel",contentRetriever = "contentRetriever")
public interface RagAiAssistant {

    @SystemMessage("You are a polite assistant")
    String chat(@UserMessage String userMessage);
}


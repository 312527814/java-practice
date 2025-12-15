package org.example.aiservices;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT, chatModel = "ollamaChatModel")
public interface OllamaAiService {

    @SystemMessage("You are a polite assistant")
    String chat(String userMessage);
}


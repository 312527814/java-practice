package org.example.controller;

import org.example.assistant.OllamaAiAssistant;
import org.example.assistant.OpenAiAssistant;
import org.example.assistant.QwenAiAssistant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/assistant")
class AssistantController {

    @Autowired
    private  OpenAiAssistant openAiAssistant;

    @Autowired
    private OllamaAiAssistant ollamaAiAssistant;

    @Autowired
    private QwenAiAssistant qwenAiAssistant;

    @GetMapping("/openai/chat")
    public String openaiChat(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        return openAiAssistant.chat(message);
    }

    @GetMapping("/ollama/chat")
    public String ollamaChat(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        return ollamaAiAssistant.chat(message);
    }

    @GetMapping("/qwen/chat")
    public String qwenChat(@RequestParam(value = "message", defaultValue = "Hello") String message,@RequestParam(value = "userId")String userId) {
        return qwenAiAssistant.chat(message,userId);
    }
}
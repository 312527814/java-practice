package org.example.controller;

import org.example.aiservices.OllamaAiService;
import org.example.aiservices.OpenAiService;
import org.example.aiservices.QwenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/assistant")
class AssistantController {

    @Autowired
    private OpenAiService openAiService;

    @Autowired
    private OllamaAiService ollamaAiService;

    @Autowired
    private QwenAiService qwenAiService;

    @GetMapping("/openai/chat")
    public String openaiChat(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        return openAiService.chat(message);
    }

    @GetMapping("/ollama/chat")
    public String ollamaChat(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        return ollamaAiService.chat(message);
    }

    @GetMapping("/qwen/chat")
    public String qwenChat(@RequestParam(value = "message", defaultValue = "Hello") String message,@RequestParam(value = "userId")String userId) {
        return qwenAiService.chat(message,userId);
    }
}
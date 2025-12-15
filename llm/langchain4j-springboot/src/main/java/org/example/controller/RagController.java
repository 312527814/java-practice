package org.example.controller;

import org.example.assistant.OllamaAiAssistant;
import org.example.assistant.OpenAiAssistant;
import org.example.assistant.QwenAiAssistant;
import org.example.assistant.RagAiAssistant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/rag")
class RagController {


    @Autowired
    private RagAiAssistant ragAiAssistant;

    @PostMapping("/chat")
    public String chat(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        return ragAiAssistant.chat(message);
    }

}
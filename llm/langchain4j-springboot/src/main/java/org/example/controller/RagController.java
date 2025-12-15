package org.example.controller;

import org.example.aiservices.RagAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/rag")
class RagController {


    @Autowired
    private RagAiService ragAiService;

    @PostMapping("/chat")
    public String chat(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        return ragAiService.chat(message);
    }

}
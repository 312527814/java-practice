package org.example.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/chatclient")
public class ChatClientController {

    ChatClient chatClient;

   /* public ChatClientController(ChatClient.Builder builder) {
        this.chatClient= builder.build();
    }*/

    @GetMapping("/openai/chat")
    public String model(@RequestParam(value = "message", defaultValue = "Hello") String message) {

        ChatClient.ChatClientRequestSpec prompt = chatClient.prompt(message);
        return  prompt.call().content();
    }
}
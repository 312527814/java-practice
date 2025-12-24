package org.example.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@RestController
@RequestMapping(value = "/mcp")
public class MCPController {
    ChatClient chatClient;
    @Autowired
    ToolCallbackProvider mcpTools;

    public MCPController(ChatClient.Builder builder ) {
        this.chatClient=builder.build();
    }
    @GetMapping("/Fluxcall")
    public Flux<String> Fluxcall(@RequestParam(value = "message", defaultValue = "北京有多少叫刘德华的人") String message) {

        Flux<String> content = chatClient.prompt()
                .tools(mcpTools)
                .user(message)
                .stream()
                .content();
        return content;
    }
}

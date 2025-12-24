package org.example.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

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
                .toolCallbacks(mcpTools)
                .user(message)
                .stream()
                .content();
        return content;
    }
    @GetMapping("/call")
    public String call(@RequestParam(value = "message", defaultValue = "北京有多少叫刘德华的人") String message) {


        ChatClient.ChatClientRequestSpec prompt = chatClient.prompt(message);
        ChatClient.CallResponseSpec call = prompt.call();
        String content = call.content();
        return  content;


    }
}

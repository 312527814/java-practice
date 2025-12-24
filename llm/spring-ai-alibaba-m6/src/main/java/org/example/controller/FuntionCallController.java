package org.example.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(value = "/funtion")
public class FuntionCallController {
    ChatClient chatClient;

    public FuntionCallController(ChatClient chatClient ) {
        this.chatClient=chatClient;
    }
    @GetMapping("/call")
    public ChatResponse call(@RequestParam(value = "message", defaultValue = "北京有多少叫刘德华的人") String message) {
        ChatResponse response = chatClient.prompt()
                .functions("locationName")
                .user(message)
                .call()
                .chatResponse();
        return response;
    }

    @GetMapping("/call/memory")
    public ChatResponse memory(@RequestParam(value = "message", defaultValue = "北京有多少叫刘德华的人，以及我想去新疆") String message) {

        //初始化基于内存的对话记忆
        ChatMemory chatMemory = new InMemoryChatMemory();

        //对话记忆的唯一标识
        String conversantId = UUID.randomUUID().toString();

        ChatResponse response = chatClient
                .prompt()
                .advisors(new MessageChatMemoryAdvisor(chatMemory))
                .user(message)
                .functions("locationName")
                .advisors(spec -> spec.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, conversantId)
                        .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getContent();

        System.out.println("content:" + content);

        response = chatClient
                .prompt()
                .advisors(new MessageChatMemoryAdvisor(chatMemory))
                .user("可以帮我推荐一些美食吗")
                .advisors(spec -> spec.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, conversantId)
                        .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        content = response.getResult().getOutput().getContent() ;

        System.out.println("content:" + content);

        return response;
    }
}

package org.example.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@RequestMapping(value = "/ai")
public class ChatModelController {

    @Autowired
    private DashScopeChatModel chatModel;

    ChatClient chatClient;

     public ChatModelController(ChatClient.Builder builder) {
        this.chatClient= builder.build();
    }


    @GetMapping("/chat")
    public String chat(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        return  chatModel.call(message);
    }
    @GetMapping("/chat/tool")
    public String tool(@RequestParam(value = "message", defaultValue = "北京有多少叫刘德华的人") String message) {
       return "";
    }

    @GetMapping("/chat/memory")
    public String memory() {
        //初始化基于内存的对话记忆
        ChatMemory chatMemory = new InMemoryChatMemory();

        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory),new SimpleLoggerAdvisor())
                .build();

        //对话记忆的唯一标识
        String conversantId = UUID.randomUUID().toString();

        ChatResponse response = chatClient
                .prompt()
                .user("我想去新疆")
                .advisors(spec -> spec.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, conversantId)
                        .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getContent();

        System.out.println("content:" + content);

        response = chatClient
                .prompt()
                .user("可以帮我推荐一些美食吗")
                .advisors(spec -> spec.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, conversantId)
                        .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        content = response.getResult().getOutput().getContent() ;

        System.out.println("content:" + content);
        return  content;
    }


}
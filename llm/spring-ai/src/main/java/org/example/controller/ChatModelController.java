package org.example.controller;

import org.example.entity.ActorFilms;
import org.example.tools.LocationNameTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/chatmodel")
public class ChatModelController {

    @Autowired
    private DeepSeekChatModel chatModel;


    @GetMapping("/deepseek/chat")
    public String deepseekModel(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
        return chatClient.prompt(message).call().content();
    }

    @GetMapping("/deepseek/entity")
    public ActorFilms entity(@RequestParam(value = "message", defaultValue = "周星驰的电影") String message) {
        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
        ActorFilms actorFilms = chatClient.prompt()
                .user(message)
                .call()
                .entity(ActorFilms.class);
        return actorFilms;
    }

    @GetMapping("/deepseek/tool")
    public String tool(@RequestParam(value = "message", defaultValue = "北京有多少叫刘德华的人") String message) {
        ToolCallback[] dateTimeTools = ToolCallbacks.from(new LocationNameTool());
        ChatOptions chatOptions = ToolCallingChatOptions.builder()
                .toolCallbacks(dateTimeTools)
                .build();
        Prompt prompt = new Prompt(message, chatOptions);

        ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();

        ChatClient.CallResponseSpec call = chatClient.prompt(prompt).call();
        return call.content();
    }

    @GetMapping("/deepseek/memory")
    public String memory() {

        ChatMemoryRepository repository = new InMemoryChatMemoryRepository();
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(10)
                .build();
        String conversationId = "007";

// First interaction
        UserMessage userMessage1 = new UserMessage("我的名字叫刘德华");
        chatMemory.add(conversationId, userMessage1);
        ChatResponse response1 = chatModel.call(new Prompt(chatMemory.get(conversationId)));
        chatMemory.add(conversationId, response1.getResult().getOutput());

// Second interaction
        UserMessage userMessage2 = new UserMessage("我叫什么名字？");
        chatMemory.add(conversationId, userMessage2);
        List<Message> messages = chatMemory.get(conversationId);
        ChatResponse response2 = chatModel.call(new Prompt(messages));
        chatMemory.add(conversationId, response2.getResult().getOutput());
        return response2.getResult().getOutput().getText();
    }

}
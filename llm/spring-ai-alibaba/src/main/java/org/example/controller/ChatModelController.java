package org.example.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.example.tools.LocationNameTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
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
        ToolCallback[] dateTimeTools = ToolCallbacks.from(new LocationNameTool());
        ChatOptions chatOptions = ToolCallingChatOptions.builder()
                .toolCallbacks(dateTimeTools)
                .build();
        Prompt prompt = new Prompt(message, chatOptions);
        ChatClient.ChatClientRequestSpec prompt1 = chatClient.prompt(prompt);
        return prompt1.call().chatResponse().getResult().getOutput().getText();
//        ReactAgent agent = ReactAgent.builder()
//                .name("my_agent")
//                .model(chatModel)
//                .tools(getUserInfoTool)
//                .saver(new MemorySaver())
//                .build();
    }


}
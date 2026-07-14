package org.example.controller;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/DeepSeek")
public class DeepSeekChatModelController {

    @Autowired
    private ReactAgent deepSeekChatAgent;;

    @GetMapping("/agent/chat")
    public AssistantMessage  chatAgent(@RequestParam(value = "message", defaultValue = "Hello") String message) throws GraphRunnerException {
        return deepSeekChatAgent.call(message);
    }


}
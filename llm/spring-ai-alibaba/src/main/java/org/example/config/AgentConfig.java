package org.example.config;

import com.alibaba.cloud.ai.agent.studio.loader.AgentLoader;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.graph.agent.Agent;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import org.example.hooks.RAGMessagesHook;
import org.example.intercepts.DynamicToolInterceptor;
import org.example.tools.BaseRequest;
import org.example.tools.BaseTool;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AgentConfig {

    @Bean
    ReactAgent dashScopeChatAgent(DashScopeChatModel chatModel,
                         List<ToolCallback> tools, VectorStore vectorStore
                         ) {
        ToolCallback baseTool = FunctionToolCallback.builder("get_base", new BaseTool())
                .description("这是一个兜底的工具，没有任何意义")
                .inputType(BaseRequest.class)
                .build();
        return ReactAgent.builder()
                .name("chat_agent")
                .model(chatModel)
                .tools(baseTool)
                .interceptors(new DynamicToolInterceptor())
                .hooks(new RAGMessagesHook(vectorStore))
                .saver(new MemorySaver())
                .build();
    }

    @Bean
    ReactAgent deepSeekChatAgent(DeepSeekChatModel chatModel,
                                 List<ToolCallback> tools, VectorStore vectorStore
    ) {
        ToolCallback baseTool = FunctionToolCallback.builder("get_base", new BaseTool())
                .description("这是一个兜底的工具，没有任何意义")
                .inputType(BaseRequest.class)
                .build();
        return ReactAgent.builder()
                .name("research_agent")
                .model(chatModel)
                .tools(baseTool)
                .interceptors(new DynamicToolInterceptor())
                .hooks(new RAGMessagesHook(vectorStore))
                .saver(new MemorySaver())
                .build();
    }

    @Bean
    AgentLoader agentLoader(List<ReactAgent> chatAgent) {
       return new AgentLoader() {
           @NotNull
           @Override
           public List<String> listAgents() {
               return chatAgent.stream().map(m->m.name()).toList();
           }

           @Override
           public Agent loadAgent(String name) {
               ReactAgent reactAgent = chatAgent.stream().filter(f -> f.name().equals(name)).findFirst().orElse(null);
               if(reactAgent==null){
                   reactAgent= chatAgent.get(0);
               }
               return reactAgent;
           }
       };
    }

    /*@Bean
    AgentLoader agentLoader() {
        return new AgentLoader() {
            @NotNull
            @Override
            public List<String> listAgents() {
                return List.of();
            }

            @Override
            public Agent loadAgent(String name) {
                return null;
            }
        };
    }*/
}

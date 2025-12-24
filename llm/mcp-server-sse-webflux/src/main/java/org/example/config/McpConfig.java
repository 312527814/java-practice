package org.example.config;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfig {
    @Bean
    public ToolCallbackProvider weatherTools(LocationNameService locationNameService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(locationNameService)
                .build();
    }

}

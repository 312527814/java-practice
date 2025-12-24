package org.example.config;

import org.example.advisor.MySimpleLoggerAdvisor;
import org.example.funtions.LocationNameService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@Configuration
public class FunctionConfig {

    @Bean
    public ChatClient getChatClient(ChatClient.Builder builder) {
        ChatClient build = builder
                .defaultAdvisors(new MySimpleLoggerAdvisor())
                .build();
        return build;
    }

    @Bean
    @Description("查询某个地方有多少个什么名字的人") // function description
    public Function<LocationNameService.Request, Integer> locationName() {
        return new LocationNameService();
    }


}

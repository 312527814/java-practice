package org.example.tools;

import org.springframework.ai.chat.model.ToolContext;

import java.util.function.BiFunction;
/*public class WeatherTool implements BiFunction<String, ToolContext, String> {
    @Override
    public String apply(String city, ToolContext toolContext) {
        return "It's always sunny in " + city + "!";
    }
}*/

public class BaseTool implements BiFunction<BaseRequest, ToolContext, String> {
    @Override
    public String apply(BaseRequest request, ToolContext toolContext) {
        System.out.println("BaseTool called for city: " + request.getId());
        return "这是一个兜底的工具，没有任何意义";
    }
}

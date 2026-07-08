package org.example.apptest;

import org.springframework.ai.chat.model.ToolContext;

import java.util.function.BiFunction;
/*public class WeatherTool implements BiFunction<String, ToolContext, String> {
    @Override
    public String apply(String city, ToolContext toolContext) {
        return "It's always sunny in " + city + "!";
    }
}*/

public class WeatherTool implements BiFunction<WeatherRequest, ToolContext, String> {
    @Override
    public String apply(WeatherRequest request, ToolContext toolContext) {
        System.out.println("WeatherTool called for city: " + request.getCity());
        return "It's always sunny in " + request.getCity() + "!";
    }
}

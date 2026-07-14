package org.example.tools;

import org.springframework.ai.chat.model.ToolContext;

import java.util.function.BiFunction;
public class PersonCountTool implements BiFunction<PersonCountRequest, ToolContext, String> {
    @Override
    public String apply(PersonCountRequest request, ToolContext toolContext) {
        System.out.println("PersonCountTool called for city: " + request.getCity());
        return "有 100人 再 " + request.getCity() + "这个城市。";
    }
}

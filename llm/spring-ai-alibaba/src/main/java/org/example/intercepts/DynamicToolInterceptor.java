package org.example.intercepts;

import com.alibaba.cloud.ai.graph.agent.interceptor.ModelCallHandler;
import com.alibaba.cloud.ai.graph.agent.interceptor.ModelInterceptor;
import com.alibaba.cloud.ai.graph.agent.interceptor.ModelRequest;
import com.alibaba.cloud.ai.graph.agent.interceptor.ModelResponse;
import org.example.tools.PersonCountRequest;
import org.example.tools.PersonCountTool;
import org.example.tools.WeatherRequest;
import org.example.tools.WeatherTool;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;

import java.util.ArrayList;
import java.util.HashMap;

public class DynamicToolInterceptor extends ModelInterceptor {

    @Override
    public String getName() {
        return "MyModelInterceptor";
    }

    @Override
    public ModelResponse interceptModel(ModelRequest request, ModelCallHandler handler) {
        System.out.println("Intercepting model call: ");
        ArrayList<String> toolNames = new ArrayList<>();
        ArrayList<ToolCallback> toolCallbacks = new ArrayList<>();
        HashMap<String, String> toolDescript = new HashMap<>();
        ToolCallback peronCountTool = FunctionToolCallback.builder("get_person_count", new PersonCountTool())
                .description("查询城市里有多少人")
                .inputType(PersonCountRequest.class)
                .build();
        toolNames.add("get_person_count");
        toolDescript.put("get_person_count", "查询城市里有多少人,严格按照这个结果来，不用猜");
        toolCallbacks.add(peronCountTool);

        ToolCallback weatherTool = FunctionToolCallback.builder("get_weather", new WeatherTool())
                .description("查询城市天气")
                .inputType(WeatherRequest.class)
                .build();
        toolNames.add("get_weather");
        toolDescript.put("get_weather", "查询城市天气");

        toolCallbacks.add(weatherTool);


        ModelRequest modified = ModelRequest.builder(request)
                .tools(toolNames)
                .toolDescriptions(toolDescript)
                .dynamicToolCallbacks(toolCallbacks)
                .build();
        return handler.call(modified);
    }
}

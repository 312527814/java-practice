package org.example.config;


import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class LocationNameService {
    @Tool(description = "查询某个地方有多少个什么名字的人")
    int LocationNameCall(@ToolParam(description = "地方") String location, @ToolParam(description = "人的名字") String name) {
        System.out.println(location + ":" + name);
        return 10;
    }
}

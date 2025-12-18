package org.example.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

public class LocationNameTool {
    @Tool(description = "查询某个地方有多少个什么名字的人")
    int call(@ToolParam(description = "地方") String location,@ToolParam(description = "人的名字")String name) {

        System.out.println(location+":"+name);
        return  10;
    }
}

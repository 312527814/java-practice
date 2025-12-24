package org.example.funtions;



import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.function.Function;

public class LocationNameService implements Function<LocationNameService.Request, Integer> {
    @Override
    public Integer apply(Request request) {
        System.out.println(request.location+":"+request.name);
        return  11;
    }

    public record Request(
            @JsonPropertyDescription("地址位置") String name,
            @JsonPropertyDescription("姓名")String location
    ) {}
}

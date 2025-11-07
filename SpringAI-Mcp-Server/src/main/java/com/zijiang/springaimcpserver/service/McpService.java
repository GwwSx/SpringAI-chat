package com.zijiang.springaimcpserver.service;


import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @ClassName McpService
 * @Description TODO
 * @Author pzykangjie
 * @Date 2025/11/7
 * @Version 1.0
 **/
@Service
public class McpService {

    @Tool(description = "根据城市名称获取天气")
    public String getWeatherByCity(String city) {
        Map<String, String> map = Map.of("北京", "11111降雨频繁，其中今天和后天雨势较强，部分地区有暴雨并伴强对流天气，需注意",
                "上海", "22222上海天气晴朗，温度在20℃至30℃之间，无降雨风险",
                "广州", "33333广州天气晴朗，温度在20℃至30℃之间，无降雨风险");
        return map.getOrDefault(city, "城市天气信息不存在");
    }
}

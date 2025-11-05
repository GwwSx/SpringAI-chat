package com.zijiang.springaidemo.tools;


import org.springframework.ai.tool.annotation.Tool;

import java.time.LocalDateTime;

/**
 * @ClassName DateTimeTool
 * @Description TODO
 * @Author pzykangjie
 * @Date 2025/11/5
 * @Version 1.0
 **/
public class DateTimeTool {


    //  @Tool(description = "获取当前时间", returnDirect = true)
    // 我是通义千问（Qwen），由阿里云研发的超大规模语言模型。我可以帮助您回答问题、创作文字、提供信息查询等服务。 当前时间是：2025年11月5日 14:39。
    @Tool(description = "获取当前时间", returnDirect = false)
    public String getCurrentTime() {
        return LocalDateTime.now().toString();
    }

    @Tool(description = "获取当前月份", returnDirect = false)
    public String getCurrentMonth() {
        return LocalDateTime.now().getMonth().toString();
    }
}

package com.zijiang.springaimcpserver.config;


import com.zijiang.springaimcpserver.service.McpService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @ClassName McpConfig
 * @Description TODO
 * @Author pzykangjie
 * @Date 2025/11/7
 * @Version 1.0
 **/
@Configuration
public class McpConfig {

    /**
     * 配置McpService作为工具回调提供程序
     * @param mcpService
     * @return
     */
    @Bean
    public ToolCallbackProvider toolCallbackProvider(McpService mcpService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(mcpService)
                .build();
    }
}

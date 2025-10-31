package com.zijiang.springaidemo.config;


import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.memory.redis.RedisChatMemoryRepository;
import lombok.Data;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName ChatConfig
 * @Description TODO
 * @Author pzykangjie
 * @Date 2025/9/30
 * @Version 1.0
 **/
@Configuration
@Data
public class ChatConfig {

    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;
    @Value("${spring.ai.dashscope.chat.options.model}")
    private String model;
    @Value("${spring.ai.dashscope.deepSeekModel}")
    private String deepSeekModel;


    /**
     * 默认单模型构建
     * @return
     */
    // @Bean
    // public DashScopeApi dashScopeApi() {
    //     return DashScopeApi.builder().apiKey(apiKey).build();
    // }

    @Bean(name = "deepSeek")
    public ChatModel deepSeek() {
        return DashScopeChatModel.builder().dashScopeApi(DashScopeApi.builder().apiKey(apiKey).build())
                .defaultOptions(DashScopeChatOptions.builder().withModel(deepSeekModel).build())
                .build();
    }

    @Bean(name = "qwen")
    public ChatModel qwen() {
        return DashScopeChatModel.builder().dashScopeApi(DashScopeApi.builder().apiKey(apiKey).build())
                .defaultOptions(DashScopeChatOptions.builder().withModel(model).build())
                .build();
    }

    @Bean(name = "deepSeekChatClient")
    public ChatClient deepSeekChatClient(@Qualifier("deepSeek") ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }

    @Bean(name = "qwenChatClient")
    public ChatClient qwenChatClient(@Qualifier("qwen") ChatModel chatModel,
                                     RedisChatMemoryRepository redisChatMemoryRepository) {
        // 聊天记忆
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(redisChatMemoryRepository)
                .maxMessages(10)
                .build();

        return ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }



}

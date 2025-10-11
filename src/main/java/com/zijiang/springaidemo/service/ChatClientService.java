package com.zijiang.springaidemo.service;


import com.zijiang.springaidemo.config.ChatConfig;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * @ClassName AIAssistantService
 * @Description TODO
 * @Author pzykangjie
 * @Date 2025/9/19
 * @Version 1.0
 **/
@Service
public class ChatClientService {

    @Resource(name = "deepSeekChatClient")
    private ChatClient deepSeekChatClient;

    @Resource(name = "qwenChatClient")
    private ChatClient qwenChatClient;

    // 构造器注入
    // private final ChatClient dashScopeChatClient;
    //
    //
    // public ChatClientService(ChatModel dashScopeChatModel) {
    //     this.dashScopeChatClient = ChatClient.builder(dashScopeChatModel).build();
    // }

    // deepSeek chat
    public String deepSeekChat(String message) {
        return deepSeekChatClient
                .prompt()
                .user(message)
                .call()
                .content();
    }

    // deepSeek stream chat
    public Flux<String> deepSeekStream(String message) {
        return deepSeekChatClient
                .prompt()
                .user(message)
                .stream()
                .content();
    }

    // qwen chat
    public String qwenChat(String message) {
        return qwenChatClient
                .prompt()
                .user(message)
                .call()
                .content();
    }

    // qwen steam
    public Flux<String> qwenStream(String message) {
        return qwenChatClient
                .prompt()
                .user(message)
                .stream()
                .content();
    }


}

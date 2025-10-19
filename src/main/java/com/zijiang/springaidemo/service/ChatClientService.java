package com.zijiang.springaidemo.service;


import com.zijiang.springaidemo.config.ChatConfig;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @ClassName AIAssistantService
 * @Description TODO
 * @Author pzykangjie
 * @Date 2025/9/19
 * @Version 1.0
 **/
@Service
@Log4j2
public class ChatClientService {

    @Resource(name = "deepSeekChatClient")
    private ChatClient deepSeekChatClient;

    @Resource(name = "qwenChatClient")
    private ChatClient qwenChatClient;

    private static final String SYSTEM_MESSAGE = "" +
            "你是一个法律助手，只回答法律问题，其它问题回复，我只能回答法律相关问题，其它无可奉告" +
            "";

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

    public Flux<String> deepSeekPromptStream(String message) {
        return deepSeekChatClient
                .prompt(SYSTEM_MESSAGE)
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

    // qwen steam toolMessage
    public String qwenStreamToolMessage(String city) {
        String chatAns = qwenChatClient.prompt()
                .user(city + "未来3天的天气")
                .call()
                .content();

        ToolResponseMessage toolResponseMessage = new ToolResponseMessage(
                List.of(new ToolResponseMessage.ToolResponse("1", "get_legal_advice", city))
        );
        String text = toolResponseMessage.getText();

        log.info("toolResponseMessage: {}", text);
        log.info("chatAns: {}", chatAns);

        return chatAns + text;
    }
}

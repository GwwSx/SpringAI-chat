package com.zijiang.springaidemo.service;


import com.zijiang.springaidemo.tools.DateTimeTool;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * @ClassName ChatToolService
 * @Description TODO
 * @Author pzykangjie
 * @Date 2025/11/5
 * @Version 1.0
 **/
@Service
public class ChatToolService {

    @Resource(name = "qwenChatClient")
    private ChatClient chatClient;

    public Flux<String> chat(String message) {
        return chatClient.prompt()
                .tools(new DateTimeTool())
                .user(message)
                .stream()
                .content();
    }


}

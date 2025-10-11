package com.zijiang.springaidemo.service;


import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * @ClassName ChatModelService
 * @Description TODO
 * @Author pzykangjie
 * @Date 2025/9/30
 * @Version 1.0
 **/

@Service
public class ChatModelService {

    @Resource(name = "deepSeek")
    private ChatModel deepSeekchatModel;

    @Resource(name = "qwen")
    private ChatModel qwenchatModel;


    public String chat(String input) {
        return deepSeekchatModel.call(input);
    }

    public Flux<String> stream(String input) {
        return deepSeekchatModel.stream(input);
    }

}

package com.zijiang.springaidemo.service;


import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
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

    public String qwenChatCall(String input) {
        return qwenchatModel.call(input);
    }

    public Flux<String> stream(String input) {
        return deepSeekchatModel.stream(input);
    }

    public Flux<String> qwenChatstream(String input) {
        return qwenchatModel.stream(input);
    }

    // qwen chatmodel userMessage 1
    // public Flux<ChatResponse> qwenChatOfUser(String question) {
    //     SystemMessage systemMessage = new SystemMessage("你是一个专业的讲故事机器人");
    //     UserMessage userMessage = new UserMessage(question);
    //     Prompt prompt = new Prompt(systemMessage, userMessage);
    //     return qwenchatModel.stream(prompt);
    // }

    //  qwen chatmodel userMessage 2
    public Flux<String> qwenChatOfUser(String question) {
        SystemMessage systemMessage = new SystemMessage("你是一个专业的讲故事机器人");
        UserMessage userMessage = new UserMessage(question);
        Prompt prompt = new Prompt(systemMessage, userMessage);
        return qwenchatModel.stream(prompt).mapNotNull(chatResponse ->
                chatResponse.getResults().get(0).getOutput().getText()
        );
    }


}

package com.zijiang.springaidemo.controller;


import com.zijiang.springaidemo.service.ChatToolService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @ClassName ChatToolController
 * @Description TODO
 * @Author pzykangjie
 * @Date 2025/11/5
 * @Version 1.0
 **/
@RestController
@RequestMapping("tools")
public class ChatToolController {


    @Resource
    private ChatToolService chatToolService;

    @GetMapping("/chat")
    public Flux<String> chat(@RequestParam(value = "message",defaultValue = "你是谁现在几点了?") String message) {
        return chatToolService.chat(message);
    }

}

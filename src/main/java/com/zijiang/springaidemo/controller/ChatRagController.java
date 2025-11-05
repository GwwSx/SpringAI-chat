package com.zijiang.springaidemo.controller;


import com.zijiang.springaidemo.service.ChatRagService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @ClassName ChatRagController
 * @Description TODO
 * @Author pzykangjie
 * @Date 2025/11/4
 * @Version 1.0
 **/
@RestController
@RequestMapping("/rag")
public class ChatRagController {

    @Resource
    private ChatRagService chatRagService;


    @GetMapping("/chat")
    public Flux<String> chat(@RequestParam("statusCode") String code,
                            @RequestParam("message") String message) {
        return chatRagService.ragChat(code, message);
    }

}

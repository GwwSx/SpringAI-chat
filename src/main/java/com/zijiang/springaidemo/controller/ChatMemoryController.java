package com.zijiang.springaidemo.controller;


import com.zijiang.springaidemo.service.ChatClientService;
import com.zijiang.springaidemo.service.ChatModelService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName ChatMemoryController
 * @Description TODO
 * @Author pzykangjie
 * @Date 2025/10/29
 * @Version 1.0
 **/
@RestController
@RequestMapping("/memory")
public class ChatMemoryController {

    @Resource
    private ChatModelService chatModelService;

    @Resource
    private ChatClientService chatClientService;



    @GetMapping("chat")
    public String chatMemoryChat(@RequestParam("msg")String msg,
                                 @RequestParam("memoryId")String memoryId){
        return chatClientService.chatMemoryChat(msg, memoryId);
    }
}

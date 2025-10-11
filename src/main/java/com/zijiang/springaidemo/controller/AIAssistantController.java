package com.zijiang.springaidemo.controller;


/**
 * @ClassName AIAssistantController
 * @Description TODO
 * @Author pzykangjie
 * @Date 2025/9/28
 * @Version 1.0
 **/
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.zijiang.springaidemo.service.ChatClientService;
import com.zijiang.springaidemo.service.ChatModelService;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class AIAssistantController {

    @Resource
    private ChatModelService chatModelService;

    @Resource
    private ChatClientService chatClientService;

    // model chat
    @GetMapping("/modelChat")
    public String modelChat(@RequestParam("msg") String input) {
        return chatModelService.chat(input);
    }

    // deepseek client chat
    @GetMapping("/clientChat")
    public String clientChat(@RequestParam("msg") String input) {
        return chatClientService.deepSeekChat(input);
    }

    // qwen client chat
    @GetMapping("/qwenClientChat")
    public String qwenChat(@RequestParam("msg") String input) {
        return chatClientService.qwenChat(input);
    }

    // model stream
    @GetMapping("/modelStream")
    public Flux<String> stream(@RequestParam("msg") String input) {
        return chatModelService.stream(input);
    }

    // client stream
    @GetMapping("/clientStream")
    public Flux<String> clientStream(@RequestParam("msg") String input) {
        return chatClientService.deepSeekStream(input);
    }

    @GetMapping("/qwenClientStream")
    public Flux<String> qwenClientStream(@RequestParam("msg") String input) {
        return chatClientService.qwenStream(input);
    }

}

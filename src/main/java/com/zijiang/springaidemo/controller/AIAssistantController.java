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
import com.zijiang.springaidemo.vo.StudentRecord;
import jakarta.annotation.Resource;
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
    // deepseek prompt stream
    @GetMapping("/deepSeek/prompt/stream")
    public Flux<String> deepSeekPromptStream(@RequestParam(value = "msg",defaultValue = "你是什么模型?") String input) {
        return chatClientService.deepSeekPromptStream(input);
    }

    // qwen chatmodel userMessage 1
    // @GetMapping("/qwenChatOfUserMessage")
    // public Flux<ChatResponse> qwenChatOfUser(@RequestParam("msg") String input) {
    //     return chatModelService.qwenChatOfUser(input);
    // }

    // qwen chatmodel userMessage 2
    @GetMapping("/qwenChatOfUserMessage")
    public Flux<String> qwenChatOfUser(@RequestParam("msg") String input) {
        return chatModelService.qwenChatOfUser(input);
    }

    @GetMapping("/qwenChatOfToolMessage")
    public String qwenChatOfToolMessage(@RequestParam("city") String city) {
        return chatClientService.qwenStreamToolMessage(city);
    }

    @GetMapping("/qwenClientStream")
    public Flux<String> qwenClientStream(@RequestParam("msg") String input) {
        return chatClientService.qwenStream(input);
    }

    @GetMapping("/qwenClientPromptTemplate")
    public Flux<String> qwenClientPromptTemplate(@RequestParam("topic") String topic,
                                                 @RequestParam("output") String output,
                                                 @RequestParam("wordCount") int wordCount) {
        return chatClientService.qwenStreamPromptTemplate(topic, output, wordCount);
    }

    @GetMapping("/qwenClientRolesPromptTemplate")
    public Flux<String> qwenClientPromptTemplate(@RequestParam("systemTopic") String systemTopic,
                                                 @RequestParam("userTopic") String userTopic) {
        return chatClientService.qwenStreamRolesPromptTemplate(systemTopic, userTopic);
    }

    @GetMapping("/qwenClientRecord/chat")
    public StudentRecord qwenClientRecord(@RequestParam("name") String name,
                                          @RequestParam("email") String email) {
        return chatClientService.qwenClientRecord(name, email);
    }

}

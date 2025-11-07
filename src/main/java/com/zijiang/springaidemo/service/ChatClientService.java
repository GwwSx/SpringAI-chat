package com.zijiang.springaidemo.service;


import com.zijiang.springaidemo.vo.StudentRecord;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

/**
 * AI聊天客户端服务类
 * 提供与不同AI模型(DeepSeek、Qwen)进行交互的功能
 * @author pzykangjie
 * @date 2025/9/19
 * @version 1.0
 */
@Service
@Log4j2
public class ChatClientService {

    /**
     * DeepSeek AI模型的聊天客户端
     */
    @Resource(name = "deepSeekChatClient")
    private ChatClient deepSeekChatClient;

    /**
     * Qwen AI模型的聊天客户端
     */
    @Resource(name = "qwenChatClient")
    private ChatClient qwenChatClient;

    /**
     * 法律助手系统提示词
     */
    private static final String SYSTEM_MESSAGE = "" +
            "你是一个法律助手，只回答法律问题，其它问题回复，我只能回答法律相关问题，其它无可奉告" +
            "";

    /**
     * 聊天提示词模板资源
     */
    @Value("classpath:templates/prompt/chat-template.txt")
    private org.springframework.core.io.Resource promptTemplate01;

    /**
     * 系统聊天提示词模板资源
     */
    @Value("classpath:templates/prompt/system-chat-template.txt")
    private org.springframework.core.io.Resource systemPromptTemplate01;

    /**
     * 使用DeepSeek模型进行同步聊天
     * @param message 用户输入的消息
     * @return AI模型返回的内容
     */
    public String deepSeekChat(String message) {
        return deepSeekChatClient
                .prompt()
                .user(message)
                .call()
                .content();
    }

    /**
     * 使用DeepSeek模型进行流式聊天
     * @param message 用户输入的消息
     * @return 流式返回的AI响应内容
     */
    public Flux<String> deepSeekStream(String message) {
        return deepSeekChatClient
                .prompt()
                .user(message)
                .stream()
                .content();
    }

    /**
     * 使用DeepSeek模型和系统提示词进行流式聊天
     * @param message 用户输入的消息
     * @return 流式返回的AI响应内容
     */
    public Flux<String> deepSeekPromptStream(String message) {
        return deepSeekChatClient
                .prompt(SYSTEM_MESSAGE)
                .user(message)
                .stream()
                .content();
    }

    /**
     * 使用Qwen模型进行同步聊天
     * @param message 用户输入的消息
     * @return AI模型返回的内容
     */
    public String qwenChat(String message) {
        return qwenChatClient
                .prompt()
                .user(message)
                .call()
                .content();
    }

    /**
     * 使用Qwen模型进行流式聊天
     * @param message 用户输入的消息
     * @return 流式返回的AI响应内容
     */
    public Flux<String> qwenStream(String message) {
        return qwenChatClient
                .prompt()
                .user(message)
                .stream()
                .content();
    }

    /**
     * 使用Qwen模型结合工具消息进行聊天
     * @param city 城市名称
     * @return 包含AI回答和工具消息的文本
     */
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

    /**
     * 使用Qwen模型和文件模板进行流式聊天
     * @param topic 主题
     * @param output 输出格式
     * @param wordCount 字数限制
     * @return 流式返回的AI响应内容
     */
    public Flux<String> qwenStreamPromptTemplate(String topic, String output, int wordCount) {
        // 方式一
        PromptTemplate promptTemplate1 = new PromptTemplate("" +
                "讲一个关于{topic}的故事" +
                "并以{output}的格式输出" +
                "字数{wordCount}左右");
        // 方式二
        PromptTemplate promptTemplate = new PromptTemplate(promptTemplate01);

        Prompt prompt = promptTemplate.create(
                Map.of("topic", "法律", "output", "markdown", "wordCount", 200)
        );

        return qwenChatClient.prompt(prompt).stream().content();
    }

    /**
     * 使用Qwen模型和角色边界划分进行流式聊天
     * @param systemTopic 系统主题
     * @param userTopic 用户主题
     * @return 流式返回的AI响应内容
     */
    public Flux<String> qwenStreamRolesPromptTemplate(String systemTopic, String userTopic) {
        // 系统提示词
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemPromptTemplate01);
        Message systemPromptMessage = systemPromptTemplate.createMessage(Map.of("systemTopic", systemTopic));
        // 用户提示词
        PromptTemplate promptTemplate = new PromptTemplate("解释一下{userTopic}");
        Message userPromptMessage = promptTemplate.createMessage(Map.of("userTopic", userTopic));

        Prompt prompt = new Prompt(List.of(systemPromptMessage, userPromptMessage));

        return qwenChatClient.prompt(prompt).stream().content();
    }


    /**
     * 使用Qwen模型获取格式化的学生记录
     * @param name 学生姓名
     * @param email 学生邮箱
     * @return 格式化的学生记录对象
     */
    public StudentRecord qwenClientRecord(String name, String email) {
        String userPrompt = "我的姓名是{name}，我的邮箱是{email}";

        // 方法1
        //  StudentRecord studentRecord = qwenChatClient.prompt().user(new Consumer<ChatClient.PromptUserSpec>() {
        //     @Override
        //     public void accept(ChatClient.PromptUserSpec promptUserSpec) {
        //         promptUserSpec.text(userPrompt)
        //                 .params(Map.of("name", name, "email", email));
        //     }
        // }).call().entity(StudentRecord.class);

        // 方法2
        StudentRecord studentRecord = qwenChatClient.prompt().user(promptUserSpec -> promptUserSpec.text(userPrompt)
                .params(Map.of("name", name, "email", email))).call().entity(StudentRecord.class);
        return studentRecord;
    }

    /**
     * 使用Qwen模型进行带记忆的聊天
     * @param msg 用户消息
     * @param memoryId 会话记忆ID
     * @return AI模型返回的内容
     */
    public String chatMemoryChat(String msg, String memoryId) {
        // return qwenChatClient.prompt(msg).advisors(new Consumer<ChatClient.AdvisorSpec>() {
        //     @Override
        //     public void accept(ChatClient.AdvisorSpec advisorSpec) {
        //         advisorSpec.param(CONVERSATION_ID, memoryId);
        //     }
        // }).call().content();

        return qwenChatClient.prompt(msg)
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, memoryId))
                .call()
                .content();
    }

    public Flux<String> mcpChat(String city) {
        return qwenChatClient.prompt(city).stream().content();
    }
}

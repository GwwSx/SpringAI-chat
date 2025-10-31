package com.zijiang.springaidemo.service;


import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import com.zijiang.springaidemo.vo.StudentRecord;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

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

    @Resource
    private EmbeddingModel embeddingModel;

    // @Resource
    // private VectorStore vectorStore;


    @Resource(name = "myRedisVectorStore")
    private RedisVectorStore redisVectorStore;



    private static final String SYSTEM_MESSAGE = "" +
            "你是一个法律助手，只回答法律问题，其它问题回复，我只能回答法律相关问题，其它无可奉告" +
            "";

    @Value("classpath:templates/prompt/chat-template.txt")
    private org.springframework.core.io.Resource promptTemplate01;

    @Value("classpath:templates/prompt/system-chat-template.txt")
    private org.springframework.core.io.Resource systemPromptTemplate01;

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

    // qwen steam promptTemplate 1
    // public Flux<String> qwenStreamPromptTemplate(String topic, String output, int wordCount) {
    //     PromptTemplate promptTemplate = new PromptTemplate("" +
    //             "讲一个关于{topic}的故事" +
    //             "并以{output}的格式输出" +
    //             "字数{wordCount}左右");
    //
    //     Prompt prompt = promptTemplate.create(
    //             Map.of("topic", "法律", "output", "markdown", "wordCount", 200)
    //     );
    //
    //     return qwenChatClient.prompt(prompt).stream().content();
    // }

    // qwen steam promptTemplate 2
    public Flux<String> qwenStreamPromptTemplate(String topic, String output, int wordCount) {
        PromptTemplate promptTemplate = new PromptTemplate(promptTemplate01);

        Prompt prompt = promptTemplate.create(
                Map.of("topic", "法律", "output", "markdown", "wordCount", 200)
        );

        return qwenChatClient.prompt(prompt).stream().content();
    }

    // 角色边界划分提示词模板
    // qwen steam roles border demarcation promptTemplate 1
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
     * 格式化输出
     * @param name
     * @param email
     * @return
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

    // chat memory
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


    // embedding
    public EmbeddingResponse embeddingChat(String msg) {
        EmbeddingResponse embeddingResponse = embeddingModel.call(
                new EmbeddingRequest(List.of("Hello World", "World is big and salvation is near", msg),
                        DashScopeEmbeddingOptions
                                .builder()
                                .withModel("text-embedding-v3")
                                .build()
                )
        );

        return embeddingResponse;
    }

    // embedding
    public void add() {
        List<Document> documents = List.of(
                new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!"),
                new Document("The World is Big and Salvation Lurks Around the Corner"),
                new Document("You walk forward facing the past and you turn back toward the future."));
        // vectorStore.add(documents);
        redisVectorStore.add(documents);
    }

    // 查找相似
    public List<Document> getSearch(String msg) {
        SearchRequest searchRequest = SearchRequest.builder().query(msg).topK(5).build();
        // List<Document> documents = vectorStore.similaritySearch(searchRequest);
        List<Document> documents = redisVectorStore.similaritySearch(searchRequest);
        return documents;
    }
}

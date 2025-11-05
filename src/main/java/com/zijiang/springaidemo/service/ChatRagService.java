package com.zijiang.springaidemo.service;

import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import java.util.regex.Pattern;

/**
 * @ClassName ChatRagService
 * @Description HTTP状态码RAG聊天服务
 * @Author pzykangjie
 * @Date 2025/11/4
 * @Version 1.0
 **/
@Service
@Log4j2
public class ChatRagService {

    @Resource(name = "qwenChatClient")
    private ChatClient chatClient;

    @Resource(name = "userRedisVectorStore")
    private RedisVectorStore userRedisVectorStore;

    @Value("classpath:templates/prompt/chat-rag-template.txt")
    private org.springframework.core.io.Resource ragPromptTemplate;

    // 状态码正则表达式模式
    private static final Pattern STATUS_CODE_PATTERN = Pattern.compile("^\\d{3}$");

    /**
     * RAG聊天方法，支持状态码专用查询和HTTP相关问题查询
     * @param code 状态码参数，用于专门查询具体状态码
     * @param message HTTP相关问题参数
     * @return 响应流
     */
    public Flux<String> ragChat(String code, String message) {
        // 判断是否为状态码查询
        boolean isStatusCodeQuery = isStatusCodeQuery(code, message);

        // 构建查询内容
        String queryContent = buildQueryContent(code, message, isStatusCodeQuery);

        // 根据查询类型设置检索参数
        RetrievalAugmentationAdvisor advisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .vectorStore(userRedisVectorStore)
                        // 状态码查询时提高相似度阈值，确保准确性
                        .similarityThreshold(isStatusCodeQuery ? 0.8 : 0.7)
                        // 状态码查询时减少返回结果数量，提高精确度
                        .topK(isStatusCodeQuery ? 1 : 3)
                        .build()
                ).build();

        log.info("开始执行RAG查询，状态码参数: {}, 问题参数: {}, 是否为状态码查询: {}, 最终查询内容: {}",
                code, message, isStatusCodeQuery, queryContent);

        return chatClient.prompt()
                .system(ragPromptTemplate)
                .user(queryContent)
                .advisors(advisor)
                .stream()
                .content()
                .doOnNext(chunk -> log.debug("返回内容片段: {}", chunk))
                .doOnComplete(() -> log.info("RAG查询完成"))
                .doOnError(error -> log.error("RAG查询出错: {}", error.getMessage()));
    }

    /**
     * 判断是否为状态码查询
     */
    private boolean isStatusCodeQuery(String code, String message) {
        // 如果code参数不为空且是有效状态码，则为状态码查询
        if (code != null && !code.trim().isEmpty() && isValidStatusCode(code.trim())) {
            return true;
        }

        // 如果message包含状态码关键词或纯数字状态码，则为状态码查询
        if (message != null && !message.trim().isEmpty() && containsStatusCode(message.trim())) {
            return true;
        }

        // 否则为普通HTTP问题查询
        return false;
    }

    /**
     * 构建查询内容
     */
    private String buildQueryContent(String code, String message, boolean isStatusCodeQuery) {
        if (isStatusCodeQuery) {
            // 状态码查询：优先使用code参数，如果没有则从message中提取
            String statusCode = code != null && !code.trim().isEmpty() ? code.trim() : extractStatusCode(message);
            return "查询HTTP状态码 " + statusCode + " 的详细信息";
        } else {
            // 普通HTTP问题查询
            return message != null ? message.trim() : "请提供HTTP相关问题";
        }
    }

    /**
     * 检查是否为有效状态码
     */
    private boolean isValidStatusCode(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }

        String trimmedInput = input.trim();
        if (STATUS_CODE_PATTERN.matcher(trimmedInput).matches()) {
            try {
                int statusCode = Integer.parseInt(trimmedInput);
                return statusCode >= 100 && statusCode <= 599;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * 检查是否包含状态码
     */
    private boolean containsStatusCode(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }

        String trimmedInput = input.trim();

        // 检查是否为纯数字状态码
        if (isValidStatusCode(trimmedInput)) {
            return true;
        }

        // 检查是否包含状态码关键词
        String lowerInput = trimmedInput.toLowerCase();
        return lowerInput.contains("状态码") ||
                lowerInput.contains("status code") ||
                lowerInput.matches(".*\\b\\d{3}\\b.*"); // 包含三位数字
    }

    /**
     * 从消息中提取状态码
     */
    private String extractStatusCode(String message) {
        if (message == null || message.trim().isEmpty()) {
            return "";
        }

        // 查找三位数字
        java.util.regex.Matcher matcher = Pattern.compile("\\b\\d{3}\\b").matcher(message.trim());
        if (matcher.find()) {
            String foundCode = matcher.group();
            if (isValidStatusCode(foundCode)) {
                return foundCode;
            }
        }

        return message.trim();
    }
}

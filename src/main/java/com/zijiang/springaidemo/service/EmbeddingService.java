package com.zijiang.springaidemo.service;


import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import com.zijiang.springaidemo.config.RedisEmbeddingModelConfig;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName EmbeddingService
 * @Description TODO
 * @Author pzykangjie
 * @Date 2025/10/31
 * @Version 1.0
 **/
@Service
@Log4j2
public class EmbeddingService {

    @Resource
    private RedisEmbeddingModelConfig redisEmbeddingModelConfig;

    @Resource(name = "productRedisVectorStore")
    private RedisVectorStore productRedisVectorStore;

    @Resource(name = "userRedisVectorStore")
    private RedisVectorStore userRedisVectorStore;

    /**
     * embedding （嵌入）  信息转换为向量表示
     * @param msg
     * @return
     */
    public EmbeddingResponse embeddingChat(String msg) {
        // 从配置中获取EmbeddingModel
        EmbeddingModel embeddingModel = redisEmbeddingModelConfig.getEmbeddingModel();
        return embeddingModel.call(
                new EmbeddingRequest(List.of("Hello World", "World is big and salvation is near", msg),
                        DashScopeEmbeddingOptions
                                .builder()
                                .withModel(redisEmbeddingModelConfig.getQwEmbeddingModel())
                                .build()
                )
        );
    }

    /**
     * 添加文档到向量存储
     */
    public void add() {
        List<Document> productDocuments = List.of(
                new Document("测试产品1"),
                new Document("测试产品12"),
                new Document("测试产品3"));
        productRedisVectorStore.add(productDocuments);

        List<Document> userDocuments = List.of(
                new Document("马冬梅"),
                new Document("马什么梅"),
                new Document("马冬什么"));

        userRedisVectorStore.add(userDocuments);
    }

    /**
     * 检索向量数据
     * @param msg
     * @return
     */
    public List<Document> getSearch(String msg) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(msg)
                // 前几位
                .topK(2)
                // 相似度阈值
                .similarityThreshold(0.7).build();
        List<Document> productDocuments = productRedisVectorStore.similaritySearch(searchRequest);
        List<Document> documents = new ArrayList<>(productDocuments);
        log.info("productDocuments: {}", productDocuments);

        List<Document> userDocuments = userRedisVectorStore.similaritySearch(searchRequest);
        log.info("userDocuments: {}", userDocuments);
        documents.addAll(userDocuments);

        return documents;
    }
}

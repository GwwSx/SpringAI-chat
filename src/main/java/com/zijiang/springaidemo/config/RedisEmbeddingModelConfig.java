package com.zijiang.springaidemo.config;



import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;

/**
 * @ClassName RedisEmbeddingModelConfig
 * @Description TODO
 * @Author pzykangjie
 * @Date 2025/10/30
 * @Version 1.0
 **/
@Configuration
@Data
@Log4j2
public class RedisEmbeddingModelConfig {

    @Value("${spring.ai.dashscope.embedding.options.model}")
    private String qwEmbeddingModel;
    @Value("${spring.ai.vectorstore.redis.initialize-schema}")
    private boolean initializeSchema;
    @Resource
    private EmbeddingModel embeddingModel;

    // 注入自定义索引1的参数（产品索引）
    @Value("${spring.ai.vectorstore.redis.product.index-name}")
    private String productIndexName;
    @Value("${spring.ai.vectorstore.redis.product.prefix}")
    private String productPrefix;

    // 注入自定义索引2的参数（用户索引）
    @Value("${spring.ai.vectorstore.redis.user.index-name}")
    private String userIndexName;
    @Value("${spring.ai.vectorstore.redis.user.prefix}")
    private String userPrefix;

    @Resource
    private RedisConfig redisConfig;

    @Bean(name = "productRedisVectorStore")
    public RedisVectorStore productRedisVectorStore() {
        JedisPooled jedisPooled = new JedisPooled(redisConfig.getHost(), redisConfig.getPort(), null, redisConfig.getPassword());
        log.info("productRedisVectorStore initialized with host: {}, port: {}, password: {}, database: {}", redisConfig.getHost(), redisConfig.getPort(), redisConfig.getPassword(), redisConfig.getDatabase());
        return RedisVectorStore.builder(jedisPooled, embeddingModel)
                .indexName(productIndexName)
                .prefix(productPrefix)
                .initializeSchema(initializeSchema)
                .build();
    }

    @Bean(name = "userRedisVectorStore")
    public RedisVectorStore userRedisVectorStore() {
        JedisPooled jedisPooled = new JedisPooled(redisConfig.getHost(), redisConfig.getPort(), null, redisConfig.getPassword());
        log.info("userRedisVectorStore initialized with host: {}, port: {}, password: {}, database: {}", redisConfig.getHost(), redisConfig.getPort(), redisConfig.getPassword(), redisConfig.getDatabase());
        return RedisVectorStore.builder(jedisPooled, embeddingModel)
                .indexName(userIndexName)
                .prefix(userPrefix)
                .initializeSchema(initializeSchema)
                .build();
    }

}

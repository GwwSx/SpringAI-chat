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

    @Value("${spring.ai.vectorstore.redis.index-name}")
    private String index;

    @Value("${spring.ai.vectorstore.redis.prefix}")
    private String prefix;

    @Resource
    private RedisConfig redisConfig;

    @Resource
    private EmbeddingModel embeddingModel;


    @Bean(name = "myRedisVectorStore")
    public RedisVectorStore redisVectorStore() {
        JedisPooled jedisPooled = new JedisPooled(redisConfig.getHost(), redisConfig.getPort(), null, redisConfig.getPassword());
        log.info("RedisVectorStore initialized with host: {}, port: {}, password: {}, database: {}", redisConfig.getHost(), redisConfig.getPort(), redisConfig.getPassword(), redisConfig.getDatabase());
        return RedisVectorStore.builder(jedisPooled, embeddingModel)
                .indexName(index)
                .prefix(prefix)
                .initializeSchema(initializeSchema)
                .build();
    }

}

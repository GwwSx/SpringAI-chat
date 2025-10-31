package com.zijiang.springaidemo.config;


import com.alibaba.cloud.ai.memory.redis.RedisChatMemoryRepository;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName RedisChatMemory
 * @Description TODO
 * @Author pzykangjie
 * @Date 2025/10/29
 * @Version 1.0
 **/
@Configuration
@Data
@Log4j2
public class RedisChatMemoryConfig {

    @Resource
    private RedisConfig redisConfig;

    @Bean
    public RedisChatMemoryRepository redisChatMemoryRepository() {
        log.info("host:{}", redisConfig.getHost());
        log.info("port:{}", redisConfig.getPort());
        log.info("password:{}", redisConfig.getPassword());
        log.info("database:{}", redisConfig.getDatabase());
        return RedisChatMemoryRepository.builder()
                .host(redisConfig.getHost())
                .port(redisConfig.getPort())
                .password(redisConfig.getPassword())
                .build();
    }

}

package com.zijiang.springaidemo.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @ClassName RedisConfig
 * @Description TODO
 * @Author pzykangjie
 * @Date 2025/10/30
 * @Version 1.0
 **/
@Configuration
@Data
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private Integer port;
    @Value("${spring.data.redis.password}")
    private String password;
    @Value("${spring.data.redis.database}")
    private String database;

}

package com.zijiang.springaidemo.config;


import com.zijiang.springaidemo.util.FixedEncryptionUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.security.Security;
import java.util.List;

/**
 * @ClassName RagConfig
 * @Description TODO
 * @Author pzykangjie
 * @Date 2025/11/4
 * @Version 1.0
 **/
@Configuration
@Log4j2
public class RagConfig {

    @Value("classpath:HTTP状态码完整对照表.md")
    private String opsContent;

    @Value("${encryption.secret-key}")
    private String secretKey;

    @Resource
    private RedisEmbeddingModelConfig redisEmbeddingModelConfig;

    @Resource
    private RedisTemplate<String, String> redisTemplate;


    @PostConstruct
    public void init() throws Exception {
        log.info("RAG预热开始...");
        // 1. 读取ops.txt文件内容
        TextReader textReader = new TextReader(opsContent);
        // 一个意思，都是设置字符集为UTF-8
        // textReader.setCharset(StandardCharsets.UTF_8);
        textReader.setCharset(Charset.defaultCharset());

        // 2. 对ops.txt文件内容进行切分
        List<Document> transform = new TokenTextSplitter().transform(textReader.read());

        // 3. 加密&&去重源文件的重复写入问题
        // 3.1 获取ops.txt文件中的source元数据
        String sourceFileName = textReader.getCustomMetadata().get("source").toString();
        log.info("加载源文件名: {}", sourceFileName);
        // 3.2 对source文件名进行加密
        String encrypt = FixedEncryptionUtil.encrypt(sourceFileName, secretKey);
        // 3.3 构建redis key
        String redisKey = "vector:" + encrypt;
        // 3.4 检查redis中是否已存在该key
        Boolean isExist = redisTemplate.opsForValue().setIfAbsent(redisKey, "1");
        // 3.5 如果键不存在，则写入索引
        if (Boolean.TRUE.equals(isExist)) {
            // 键不存在
            RedisVectorStore redisVectorStore = redisEmbeddingModelConfig.userRedisVectorStore();
            redisEmbeddingModelConfig.userRedisVectorStore().add(transform);
        }else{
            log.info("redis key:{} 已存在", redisKey);
        }
        // // 3. 写入用户索引
        // redisEmbeddingModelConfig.userRedisVectorStore().add(transform);
        log.info("opsContent预热完成...");
    }

}

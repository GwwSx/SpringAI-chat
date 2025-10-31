package com.zijiang.springaidemo.controller;


import com.zijiang.springaidemo.service.ChatClientService;
import com.zijiang.springaidemo.service.EmbeddingService;
import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName ChatEmbeddingController
 * @Description TODO
 * @Author pzykangjie
 * @Date 2025/10/30
 * @Version 1.0
 **/
@RestController
@RequestMapping("/embedding")
public class ChatEmbeddingController {

    @Resource
    private EmbeddingService embeddingService;


    @GetMapping("")
    public EmbeddingResponse qwenEmbedding(@RequestParam("msg") String msg) {
        return embeddingService.embeddingChat(msg);
    }

    @GetMapping("/add")
    public String add() {
        embeddingService.add();
        return "success";
    }

    @GetMapping("/getSearch")
    public List<Document> getSearch(@RequestParam("msg") String msg) {
        return embeddingService.getSearch(msg);
    }


}

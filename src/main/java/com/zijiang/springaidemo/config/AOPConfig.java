package com.zijiang.springaidemo.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName AOPConfig
 * @Description TODO
 * @Author pzykangjie
 * @Date 2025/11/5
 * @Version 1.0
 **/

@Data
@Configuration
public class AOPConfig {

    @Value("${aop.debug.controller}")
    private Boolean controller;

    @Value("${aop.debug.service}")
    private Boolean service;

    @Value("${aop.debug.log-record}")
    private Boolean logRecord;


}

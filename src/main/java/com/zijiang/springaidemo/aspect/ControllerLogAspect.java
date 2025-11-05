package com.zijiang.springaidemo.aspect;


import com.zijiang.springaidemo.config.AOPConfig;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * @ClassName ControllerLogAspect
 * @Description TODO
 * @Author pzykangjie
 * @Date 2025/11/5
 * @Version 1.0
 **/
@Aspect
@Component
@Log4j2
public class ControllerLogAspect {

    @Resource
    private AOPConfig aopConfig;

    // execution(public * com.example..controller..*(..))：拦截所有controller包下的公共方法
    // @annotation(com.example.LogRecord)：拦截所有被自定义注解标记的方法
    @Pointcut("execution(public * com.zijiang.springaidemo.controller.*.*(..))")
    public void controllerMethods() {
    }

    @Around("controllerMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!aopConfig.getController()) {
            // 不开启，直接执行原方法
            return joinPoint.proceed();
        }

        // 记录开始时间
        long start = System.currentTimeMillis();
        // 获取目标类的 Class 对象
        Class<?> targetClass = joinPoint.getTarget().getClass();

        // 获取当前请求的 HttpServletRequest 对象
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 提取请求的 URL、方法、IP 地址、处理方法和参数
        String url = request.getRequestURL().toString();
        String method = request.getMethod();
        String ip = request.getRemoteAddr();
        String classMethod = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();


        log.info("\n====== 🌐 请求信息 ======\n" +
                        "🎯 URL        : {}\n" +
                        "🛠️ Method     : {}\n" +
                        "📍 IP Address : {}\n" +
                        "🎯 Handler    : {}\n" +
                        "📥 Request    : {}\n" +
                        "==========================",
                url, method, ip, classMethod, args);

        // 尝试执行目标方法，并记录执行结果或异常信息
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            // 记录异常信息，并重新抛出异常
            log.error("\n====== 🌐 ❌ 异常信息 ======\n" +
                            "🎯 Handler    : {}\n" +
                            "🛑 Error      : {}\n" +
                            "==========================",
                    classMethod, e.getMessage(), e);
            throw e;
        }

        // 记录结束时间，并计算耗时
        long end = System.currentTimeMillis();
        // 记录响应信息
        log.info("\n====== 🌐 ✅ 响应信息 ======\n" +
                        "🎯 Handler    : {}\n" +
                        "📤 Response   : {}\n" +
                        "⏱️ 耗时        : {} ms\n" +
                        "==========================",
                classMethod, result, end - start);

        return result;

    }

}

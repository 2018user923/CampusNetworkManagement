package com.example.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
/**
 * @Description 切面编程打印入参及返回值
 * @Author <247702560@qq.com>
 * @Date 2021/6/1 10:28
 */
public class AspectOutLog {

    @Pointcut(value = "execution(* com.example.demo..*(..)) && !execution(* com.example.demo.ws..*(..))")
    public void method() {

    }

    @Around("method()")
    public Object syncLogInfo(ProceedingJoinPoint pjp) {
        Object response = null;
        Class<?> targetClass = pjp.getTarget().getClass();
        Signature signature = pjp.getSignature();
        String targetFunctionName = signature.getName();
        String targetClassName = targetClass.getSimpleName();
        log.info("{}#{} 任务执行开始,args={}", targetClassName, targetFunctionName, Arrays.toString(pjp.getArgs()));
        try {
            response = pjp.proceed();
            log.info("{}#{} 任务执行结束,response={}", targetClassName, targetFunctionName, response);
        } catch (Throwable throwable) {
            log.info("{}#{} 任务执行异常,response={}", targetClassName, targetFunctionName, response, throwable);
        }
        return response;
    }
}

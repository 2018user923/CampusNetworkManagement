package com.example.demo.util;

import com.example.demo.domain.User;
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
public class MyAspect {

    @Pointcut(value = "execution(* com.example.demo.controller..*(..))")
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
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return response;
    }
}

package com.example.demo.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class MyAspect {

    @Pointcut(value = "execution(* com.example.demo..*(..))")
    public void method() {

    }

    @Around("method()")
    public Object syncLogInfo(ProceedingJoinPoint pjp) {
        Object proceed = null;
        Class<?> targetClass = pjp.getTarget().getClass();
        Signature signature = pjp.getSignature();
        String targetFunctionName = signature.getName();
        String targetClassName = targetClass.getSimpleName();
        String msg = String.format("%s#%s 任务执行开始,args=%s", targetClassName, targetFunctionName, Arrays.toString(pjp.getArgs()));
        System.out.println(msg);
        try {
            proceed = pjp.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return proceed;
    }
}

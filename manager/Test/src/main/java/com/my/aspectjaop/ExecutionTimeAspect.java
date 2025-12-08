package com.my.aspectjaop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.ProceedingJoinPoint;

@Aspect
public class ExecutionTimeAspect {

    @Around("execution(* MyService.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        System.out.println(".....before......");
        Object proceed = joinPoint.proceed(); // Call the actual method

        long executionTime = System.currentTimeMillis() - startTime;

//        System.out.printf("%s executed in %dms%n", joinPoint.getSignature(), executionTime);
        System.out.println(".....after......");
        return proceed;
    }
}
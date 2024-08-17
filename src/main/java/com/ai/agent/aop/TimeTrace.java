package com.ai.agent.aop;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Component
@Aspect
@Slf4j
public class TimeTrace {

    // controller
    @Around("execution(* com.ai.agent..controller..*(..))")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        long start = System.currentTimeMillis();
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.split("-", 5)[0];
        log.info(">>> START [{}] : {} {}", uuid, request.getMethod(), request.getRequestURL());

        try {
            return joinPoint.proceed();
        } finally {
            long finish = System.currentTimeMillis();
            long timeMs = finish - start;

            log.info("<<< END [{}] : {} {}: {}ms", uuid, request.getMethod(), request.getRequestURL(), timeMs);
        }
    }

}

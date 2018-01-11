package com.liu.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class HttpAspect {

    private static final Logger logger = LoggerFactory.getLogger(HttpAspect.class);

    @Pointcut("execution(public * com.liu.controller.*.*(..))")
    public void log(){
    }

    @Before("log()")
    public void beforeLog(JoinPoint joinPoint){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //URL
        HttpServletRequest request = attributes.getRequest();
        logger.info("url:{}",request.getRequestURL());
        //METHOD
        logger.info("method:{}",request.getMethod());
        //ip
        logger.info("ip:{}",request.getRemoteAddr());
        //args
        StringBuilder builder = new StringBuilder();
        Object[] args = joinPoint.getArgs();
        for (Object object :args) {
            builder.append(object);
            builder.append(",");
        }
       logger.info("args:{}", joinPoint.getArgs());
        //类名
        logger.info("class_name:{}", joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName());
    }

    @After("log()")
    public void aferLog(){
    }

    @AfterReturning(pointcut = "log()",returning = "object")
    public void doAfterReturn(Object object){
        logger.info("response:{}",object);
    }
}

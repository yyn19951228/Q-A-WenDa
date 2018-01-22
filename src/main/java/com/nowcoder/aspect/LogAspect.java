package com.nowcoder.aspect;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect

@Component // ?
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    // means before execute all the controller in com.nowcoder/controller/
    // the first * means return value
    // the second * means controller class under /controller like IndexController
    // the third * means the method in controller class
    @Before("execution(* com.nowcoder.controller.*.*(..))")
    public void beforeMethod() {
        // all the controller will call beforeMethod() before their own service
        logger.info("before method");
    }

    @After("execution(* com.nowcoder.controller.*.*(..))")
    public void afterMethod() {
        // all the controller will call afterMethod() after their own service
        logger.info("after method");
    }

}

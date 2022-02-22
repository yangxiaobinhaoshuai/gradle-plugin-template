package me.yangxiaobin.sample.aspectj.aspect;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class SampleAspect {

    @After("execution(public * *(..))")
    public void afterExecute () {
        System.out.println("---> sample aspect afterExecute ");
    }

}

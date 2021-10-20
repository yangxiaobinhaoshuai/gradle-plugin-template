package me.yangxiaobin.androidapp.aspect;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class JavaAspect {

    @After("execution(public * *(..))")
    public void afterExecute() {
        System.out.println("---> sample aspect afterExecute ");
    }

}

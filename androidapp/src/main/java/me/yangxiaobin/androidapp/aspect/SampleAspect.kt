package me.yangxiaobin.androidapp.aspect

import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect


@Aspect
class SampleAspect {

    @After("execution(public * *(..))")
    fun doBefore() {
        println("-----> Im Before Aspect")
    }
}

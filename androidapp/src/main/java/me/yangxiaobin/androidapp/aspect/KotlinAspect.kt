package me.yangxiaobin.androidapp.aspect

import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect


@Aspect
class KotlinAspect {

    @After("execution(private * *(..))")
    fun doBefore() {
        println("-----> Im Before Aspect")
    }
}

package me.yangxiaobin.test

import org.junit.Test

class RegexTest {


    @Test
    fun regexTest() {
        val regex = "annotation-.+.jar"
        assert(regex.toRegex().matches("annotation-1.2.0.jar"))
    }
}

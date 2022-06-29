package me.yangxiaobin.test

import me.yangxiaobin.lib.ext.toFormat
import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.log.log
import org.junit.Test

class ExtTest {

    val logV = TestLogger.log(LogLevel.VERBOSE, "ExtTest/")

    @Test
    fun timeExtTest() {
        val timeLong: Long = 60_123L
        val formatStr = timeLong.toFormat()
        assert(formatStr == "00h:01m:00s:123ms")
    }

}

package me.yangxiaobin.logger

import me.yangxiaobin.logger.core.LogLevel
import me.yangxiaobin.logger.elements.GlobalTagPrefixLogElement

class LoggerTest {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

            RawLogger.i("RawTAG", "RawInfoMessage")

            RawLogger.e("RawTAG", "RawErrorMessage")

            RawLogger.clone(globalTagPrefix = "NeatPrefix").e("RawTAG", "RawErrorMessage")

            RawLogger
                .clone(GlobalTagPrefixLogElement("TestPrefix==>"))
                .e("CloneTAG", "cloneMessage")

            val logI = RawLogger
                .clone(GlobalTagPrefixLogElement("TestPrefix/"))
                .log(LogLevel.INFO, "LogITag")


            logI("Hello Logger!")
            logI("Logger dump :${RawLogger.dumpDomainContext()}")
        }
    }
}

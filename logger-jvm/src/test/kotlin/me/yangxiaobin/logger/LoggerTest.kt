package me.yangxiaobin.logger

import me.yangxiaobin.logger.core.LogFacade
import me.yangxiaobin.logger.core.LogLevel
import me.yangxiaobin.logger.elements.GlobalTagPrefixLogElement
import me.yangxiaobin.logger.elements.LogLevelLogElement
import me.yangxiaobin.logger.elements.LogPrinterLogElement
import me.yangxiaobin.logger.uitlity.LogPrinter

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

            val printer =  object :LogPrinter{
                override fun print(
                    level: LogLevel,
                    tag: String,
                    message: String,
                    throwable: Throwable?
                ) {
                    // TODO
                }
            }


            val newLogger = L
                .clone(
                    globalTagPrefix = "Prefix abc",
                    newLogContext = LogLevelLogElement(LogLevel.INFO) + LogPrinterLogElement(printer) ,
                )
            logI("Logger dump :${newLogger.dumpContext()}")
        }
    }

    object L : LogFacade by RawLogger
}

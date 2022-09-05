package me.yangxiaobin.sample.log

import me.yangxiaobin.lib.ext.currentProcessId
import me.yangxiaobin.lib.ext.currentWorkPath
import me.yangxiaobin.logger.RawLogger
import me.yangxiaobin.logger.disk_writer.DiskWriterManager
import me.yangxiaobin.logger.disk_writer.OnPostLoggingInterceptor
import me.yangxiaobin.logger.elements.InterceptorLogElement
import java.lang.management.ManagementFactory
import kotlin.concurrent.thread

class LogTest {

    companion object {

        private const val LOG_TAG = "LogTest"

        private val logger = RawLogger.clone(newLogContext = InterceptorLogElement(OnPostLoggingInterceptor(DiskWriterManager::addLog)))

        private fun logD(m: String) = logger.d(LOG_TAG, "$m.")

        private fun logE(m: String) = logger.e(LOG_TAG, "$m.")


        @JvmStatic
        fun main(args: Array<String>) {

            val tempPath = "samples/app/temp/build.log"

            val actualLogPath = "$currentWorkPath/$tempPath"

            println("---> LogTest, actualLogPath :$actualLogPath , ${ManagementFactory.getRuntimeMXBean().name}")

            DiskWriterManager.setConfig {
                this.logFileName = actualLogPath
                this.pid = currentProcessId
                // TODO
                this.processName = ""
            }

            DiskWriterManager.startSession()

            for (i in 1..10) {
                logD("----> logger i :$i")
            }

            thread(name = "Async-1", isDaemon = true) {
                var count = 0
                while (true) {
                    Thread.sleep(500)
                    logD("I'm async: ${count++}")
                }
            }


            // Java stop
            thread {
                Thread.sleep(3_000)
                //DiskWriterManager.stopSession()
            }


        }
    }
}

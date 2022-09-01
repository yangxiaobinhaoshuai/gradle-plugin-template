package me.yangxiaobin.sample.log

import me.yangxiaobin.logger.disk_writer.DiskWriter

class LogTest {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            println("---> LogTest")

            DiskWriter.setConfig {

                this.logFileName = "/Users/yangxiaobin/Downloads/adadsa.log"
                this.pid = 123
                this.processName = "lalalal"
            }

            DiskWriter.startSession()

        }
    }
}

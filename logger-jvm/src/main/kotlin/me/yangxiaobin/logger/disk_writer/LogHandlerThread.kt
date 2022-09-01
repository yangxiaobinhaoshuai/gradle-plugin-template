package me.yangxiaobin.logger.disk_writer

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

interface LogHandler {

    fun handleLog(info: LogInfo)

    fun startLoopHandling()
}

class LogHandlerThread : Thread(), LogHandler {

    private val queue: BlockingQueue<LogInfo> = LinkedBlockingQueue()

    override fun handleLog(info: LogInfo) {
        queue.add(info)
    }


    override fun run() {
        super.run()
        val newInfo: LogInfo = queue.poll()

    }

    override fun startLoopHandling() {
        this.start()
    }


}

package me.yangxiaobin.logger.disk_writer

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

interface LogHandler {

    fun handleLog(logInfoBytes: ByteArray)

    fun startLoopHandling()
}

class LogHandlerThread : Thread(), LogHandler {

    private val queue: BlockingQueue<ByteArray> = LinkedBlockingQueue()


    init {
        name = "LogDiskHandler"
        isDaemon = true
    }

    override fun handleLog(logInfoBytes: ByteArray) {
        queue.add(logInfoBytes)
    }


    override fun run() {
        super.run()

        DiskWriterManager.logFileWriter.use { writer->
            while (true) {
                val logInfoByteArray: ByteArray = queue.poll(Long.MAX_VALUE, TimeUnit.DAYS) ?: return
                println("----> info : ${String(logInfoByteArray)}")
                writer.write(String(logInfoByteArray))
            }
        }

    }

    override fun startLoopHandling() {
        this.start()
    }


}

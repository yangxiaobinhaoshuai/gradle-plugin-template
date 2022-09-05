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

        // Remember flush or close the stream.
        DiskWriterManager.logFileWriter.use { writer->
            while (true) {
                val logInfoByteArray: ByteArray = queue.poll(Long.MAX_VALUE, TimeUnit.DAYS) ?: return
                writer.write(String(logInfoByteArray))
                writer.flush()
            }
        }

    }

    override fun startLoopHandling() {
        this.start()
    }


}

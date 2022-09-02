package me.yangxiaobin.logger.disk_writer

import me.yangxiaobin.logger.uitlity.LogMessageMeta
import java.io.File
import java.io.Writer
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.properties.Delegates


data class DiskWriterConfig(
    var logFileName: String? = null,
    var pid: Long = -1L,
    var processName: String? = null,
)


data class LogInfo(
    val level: String,
    val tag: String,
    val message: String,
    val createTimestamp: Long,
    val pid: Long,
    val processName: String,
    val tid: Long,
    val threadName: String,
)

object DiskWriterManager {

    private var isActive = AtomicBoolean(false)

    private val config by lazy { DiskWriterConfig() }

    /**
     * alias
     */
    private val curThread get() = Thread.currentThread()

    private var logFile: File by Delegates.notNull()

    public val logFileWriter: Writer by lazy { logFile.printWriter() }

    private val diskLogHandler: LogHandler by lazy { LogHandlerThread() }

    private val diskFormatter: DiskLogFormatter by lazy { SimpleAssembleFormatter() }

    public fun setConfig(configOperation: DiskWriterConfig.() -> Unit) {
        configOperation.invoke(config)
    }

    private fun checkConfig(cfg: DiskWriterConfig) {
        cfg.logFileName ?: throw IllegalArgumentException("U Must set log file name")
        if (cfg.pid < 0) throw IllegalArgumentException("U Must set pid")
        cfg.processName ?: throw IllegalArgumentException("U Must set process name")
    }

    fun startSession() {
        if (!isActive.compareAndSet(false, true)) return

        try {
            checkConfig(config)

            val path: String = requireNotNull(config.logFileName)

            val f = File(path)

            if (f.exists()) f.deleteRecursively()

            logFile = File(path).touch()

        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }

        diskLogHandler.startLoopHandling()
    }

    fun stopSession() {
        isActive.set(false)
        logFileWriter.closeSafely()
    }

    fun addLog(messageMeta: LogMessageMeta) {
        if (!isActive.get()) return
        val info = messageMeta.wrap2LogInfo()
        val arr = diskFormatter.format(info)
        diskLogHandler.handleLog(arr)
    }

    private fun LogMessageMeta.wrap2LogInfo(): LogInfo {
        return LogInfo(
            level = this.level.toString(),
            tag = this.tag,
            message = this.message,
            createTimestamp = System.currentTimeMillis(),
            pid = config.pid,
            processName = config.processName!!,
            tid = curThread.id,
            threadName = curThread.name,
        )
    }

}

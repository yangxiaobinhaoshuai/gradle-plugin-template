package me.yangxiaobin.logger.disk_writer

import me.yangxiaobin.logger.uitlity.LogMessageMeta
import java.io.File
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

    val Pid: Long,
    val ProcessName: String,
    val Tid: Long,
    val ThreadName: String,
)

object DiskWriter {

    private var isActive = false

    private val config by lazy { DiskWriterConfig() }

    /**
     * alias
     */
    private val curThread get() = Thread.currentThread()

    private var logFile: File by Delegates.notNull()

    public fun setConfig(configOperation: DiskWriterConfig.() -> Unit) {
        configOperation.invoke(config)
    }

    private fun checkConfig(cfg: DiskWriterConfig) {
        cfg.logFileName ?: throw IllegalArgumentException("U Must set log file name")
        if (cfg.pid < 0) throw IllegalArgumentException("U Must set pid")
        cfg.processName ?: throw IllegalArgumentException("U Must set process name")
    }

    fun addLog(messageMeta: LogMessageMeta) {
        if (!isActive) return
        val info = messageMeta.wrap2LogInfo()
    }

    fun startSession() {
        isActive = true

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
    }

    fun stopSession() {
        isActive = false
    }

    private fun LogMessageMeta.wrap2LogInfo(): LogInfo {
        return LogInfo(
            level = this.level.toString(),
            tag = this.tag,
            message = this.message,

            createTimestamp = System.currentTimeMillis(),
            Pid = config.pid,
            ProcessName = config.processName!!,
            Tid = curThread.id,
            ThreadName = curThread.name,
        )
    }

}

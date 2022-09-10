package me.yangxiaobin.logger.disk_writer

import java.text.SimpleDateFormat
import java.util.*

interface DiskLogFormatter {

    fun format(info: LogInfo): ByteArray

}

class SimpleAssembleFormatter : DiskLogFormatter{

    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.getDefault())

    /**
     * e.g. 2022-09-02 11:16:50.302|35427|1 main|DEBUG|LogTest|----> logger i :7.
     */
    override fun format(info: LogInfo): ByteArray {

        val dateString: String = sdf.format(Date(info.createTimestamp))
        val tag = info.tag
        val tid = info.tid
        val threadName = info.threadName
        val pid = info.pid
        val processName = info.processName
        val message = info.message
        val level = info.level

        val logString = "$dateString|$pid|$tid($threadName)|$level|$tag|$message$CARRIAGE_RETURN"

        return logString.toByteArray()
    }

}

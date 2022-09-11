package me.yangxiaobin.lib.transform_v3

import com.android.zipflinger.BytesSource
import com.android.zipflinger.ZipArchive
import me.yangxiaobin.lib.JUCExecutorService
import me.yangxiaobin.lib.executor.InternalExecutor
import me.yangxiaobin.lib.ext.*
import me.yangxiaobin.lib.log.InternalLogger
import me.yangxiaobin.lib.log.LogAware
import me.yangxiaobin.lib.log.LogDelegate
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.Executor
import java.util.zip.Deflater


sealed interface TransformTicket {
    val from: File
    val to: File
    operator fun component1() = from
    operator fun component2() = to

    fun exchange(from: File, to: File): TransformTicket

    override fun toString(): String
}

data class ChangedFileTicket(override val from: File, override val to: File) : TransformTicket{
    override fun exchange(from: File, to: File): TransformTicket = this.copy(from = from, to = to)

    override fun toString(): String {
        return "from=$from to=$to"
    }
}
data class DeleteTicket(override val from: File, override val to: File) : TransformTicket{
    override fun exchange(from: File, to: File): TransformTicket = this.copy(from = from, to = to)

    override fun toString(): String {
        return "from=$from to=$to"
    }
}


interface TransformBus{

    fun takeTickets(tickets: List<TransformTicket>)
}


object TransformTicketImpl : TransformBus {

    // TODO should be di
    private val copyTransformer: FileTransformer by lazy { CopyTransformer }
    private val jarTransformer: FileTransformer by lazy { JarTransformer }
    private val classTransformer: FileTransformer by lazy { ClassTransformer }

    private val engine: TransformEngine by lazy { TransformEngineImpl }

    override fun takeTickets(tickets: List<TransformTicket>) {

        val rs: Sequence<Runnable> = sequence {

            fun TransformTicket.detailed(detailFile: File): TransformTicket {
                val detailOutputFile = File(this.to, detailFile.relativeTo(this.from).path)
                return this.exchange(detailFile, detailOutputFile)
            }

            tickets.forEach { ticket ->

                val from = ticket.from

                when {
                    from.isJarFile() -> yield(Runnable { jarTransformer.transform(ticket) })

                    from.isDirectory -> {

                        from.walkTopDown().forEach { childFile: File ->

                            val actualTicket = ticket.detailed(childFile)

                            if (childFile.isClassFile())
                                yield(Runnable { classTransformer.transform(actualTicket) })
                            else
                                yield(Runnable { copyTransformer.transform(actualTicket) })

                        }

                    }

                    else -> throw IllegalArgumentException("UnSupport file format :$from.")
                }
            }
        }

        rs.iterator().forEach(engine::submitTransformActionSync)
    }

}

interface TransformEngine : Executor {

    fun submitTransformActionSync(runnable: Runnable)

    override fun execute(command: Runnable) {
        submitTransformActionSync(command)
    }

}


object TransformEngineImpl : TransformEngine {

    private val executor: JUCExecutorService = InternalExecutor.fixed

    override fun submitTransformActionSync(runnable: Runnable) {
        executor.submit(runnable).get()
    }

}


private const val LOG_TAG = "V3Transformer"
private val transformerLogDelegate = LogDelegate(InternalLogger, LOG_TAG)

sealed interface FileTransformer {

    /**
     * 有可能是被删除的文件
     */
    fun transform(ticket: TransformTicket)
}

object CopyTransformer : FileTransformer, LogAware by transformerLogDelegate {

    override fun transform(ticket: TransformTicket) {
        val (input, output) = ticket

        //logI("${curThread.name} copy from: $input to $output.")

        if (ticket !is DeleteTicket) input.safeCopyTo(output)
    }
}

object JarTransformer : FileTransformer, LogAware by transformerLogDelegate {

    private val executor: JUCExecutorService = InternalExecutor.createFixed(CPU_COUNT)

    override fun transform(ticket: TransformTicket) {

        val (_, output) = ticket

        //logI("${curThread.name} jar transform from: $input to $output.")

        rewriteJar(ticket)
        if (ticket is DeleteTicket) output.delete()
    }

    private fun rewriteJar(ticket: TransformTicket) {

        val (input, output) = ticket

        val inputZipArchive = ZipArchive(input.toPath())
        val outputZipArchive by lazy { ZipArchive(output.toPath()) }

        val byteOperations: Sequence<Runnable> = sequence {

            inputZipArchive.listEntries()
                .forEach { entryName: String ->

                    //logI("process entry name :$entryName.")

                    val bf: ByteBuffer = inputZipArchive.getContent(entryName)
                    val bs = ByteArray(bf.remaining())
                    bf.get(bs)

                    val transformedBs = TransformAwareManager.getClassTransformer().invoke(ticket, bs)

                    // Just copy here
                    yield(
                        Runnable {
                            //logI("${curThread.name} write into out jar, $entryName.")
                            outputZipArchive.add(BytesSource(transformedBs, entryName, Deflater.NO_COMPRESSION))
                        }
                    )
                }
        }

        byteOperations.forEach { executor.submit(it).get() }

        inputZipArchive.close()
        outputZipArchive.close()
    }

}

object ClassTransformer : FileTransformer, LogAware by transformerLogDelegate {

    override fun transform(ticket: TransformTicket) {

        val (input, output) = ticket

        //logI("${curThread.name} class transform from: $input to $output.")

        val transformedBs = TransformAwareManager.getClassTransformer().invoke(ticket, input.readBytes())

        //input.safeCopyTo(output)
        output.touch().writeBytes(transformedBs)

        if (ticket is DeleteTicket) output.delete()
    }

}



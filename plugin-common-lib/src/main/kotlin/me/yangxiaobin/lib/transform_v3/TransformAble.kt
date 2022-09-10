package me.yangxiaobin.lib.transform_v3

import com.android.zipflinger.ZipArchive
import me.yangxiaobin.lib.JUCExecutorService
import me.yangxiaobin.lib.executor.InternalExecutor
import me.yangxiaobin.lib.ext.*
import me.yangxiaobin.lib.log.InternalLogger
import me.yangxiaobin.lib.log.LogAware
import me.yangxiaobin.lib.log.LogDelegate
import java.io.File
import java.nio.ByteBuffer


sealed interface TransformTicket {
    val from: File
    val to: File
    operator fun component1() = from
    operator fun component2() = to

    fun clone(from: File, to: File):TransformTicket

    override fun toString(): String
}

data class ChangedFileTicket(override val from: File, override val to: File) : TransformTicket{
    override fun clone(from: File, to: File): TransformTicket = this.copy(from = from, to = to)

    override fun toString(): String {
        return "from=$from to=$to"
    }
}
data class DeleteTicket(override val from: File, override val to: File) : TransformTicket{
    override fun clone(from: File, to: File): TransformTicket = this.copy(from = from, to = to)

    override fun toString(): String {
        return "from=$from to=$to"
    }
}


interface TransformBus{

    fun takeTickets(tickets:List<TransformTicket>)
}


object TransformTicketImpl : TransformBus {


    override fun takeTickets(tickets: List<TransformTicket>) {

        fun TransformTicket.toConcrete(childFile: File) = this.clone(childFile, File(this.to, childFile.relativeTo(this.from).path))

        val rs = sequence {

            tickets.map { t: TransformTicket ->

                val from = t.from

                when {
                    from.isJarFile() -> yield(Runnable { JarTransformer.transform(t) })

//                    from.isDirectory -> {
//
//                        from.walkTopDown().forEach { childFile: File ->
//
//                            val actualTicket = t.toConcrete(childFile)
//
//                            if (childFile.isClassFile())
//                                yield(Runnable { ClassTransformer.transform(actualTicket) })
//                            else
//                                yield(Runnable { CopyTransformer.transform(actualTicket) })
//
//                        }
//
//                    }

                    //else -> throw IllegalArgumentException("Illegal file :$from.")
                    else -> Unit
                }
            }
        }

        rs.iterator().forEach {
            TransformEngine.submitSync(it)
        }
    }

}


object TransformEngine {

    private val executor: JUCExecutorService = InternalExecutor.single

    fun submitSync(runnable: Runnable) {
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

        logI("${curThread.name} copy from: $input to $output.")
        //input.copyTo(output)

        // Copy only is NOT deletion
        //if (ticket !is DeleteTicket) from.safeCopyTo(to)
    }
}

object JarTransformer : FileTransformer, LogAware by transformerLogDelegate {

    private val executor: JUCExecutorService = InternalExecutor.createSingle()

    override fun transform(ticket: TransformTicket) {

        val (input, output) = ticket

        logI("${curThread.name} jar transform from: $input to $output.")

        rewriteJar(input, output)
    }

    private fun rewriteJar(input: File, output: File) {

        val inputZipArchive = ZipArchive(input.toPath())
        val outputZipArchive by lazy { ZipArchive(output.toPath()) }

        val byteOperations: Sequence<Runnable> = sequence {

            inputZipArchive.listEntries()
                .forEach { entryName: String ->

                    logI("process entry name :$entryName.")

                    val bf: ByteBuffer = inputZipArchive.getContent(entryName)
                    val bs = ByteArray(bf.remaining())
                    bf.get(bs)

                    // Just copy here
                   // yield(Runnable { println("---> write into out jar, $entryName.");outputZipArchive.add(BytesSource(bs, entryName, Deflater.NO_COMPRESSION)) })
                }
        }

        byteOperations.forEach {
            executor.submit(it).get()
        }

        inputZipArchive.close()
        outputZipArchive.close()
    }

}

object ClassTransformer : FileTransformer, LogAware by transformerLogDelegate {

    override fun transform(ticket: TransformTicket) {

        val (input, output) = ticket

        logI("${curThread.name} class transform from: $input to $output.")
        //input.copyTo(output)

        //val bs = TransformRegistry.getFoldConverter().transform(input.readBytes())


        //input.safeCopyTo(output)
       // output.touch().writeBytes(bs)

    }

}



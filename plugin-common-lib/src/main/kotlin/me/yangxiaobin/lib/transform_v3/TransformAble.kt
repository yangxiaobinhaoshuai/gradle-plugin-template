package me.yangxiaobin.lib.transform_v3

import com.android.zipflinger.BytesSource
import com.android.zipflinger.ZipArchive
import me.yangxiaobin.lib.JUCExecutorService
import me.yangxiaobin.lib.TransformRegistry
import me.yangxiaobin.lib.executor.InternalExecutor
import me.yangxiaobin.lib.ext.isClassFile
import me.yangxiaobin.lib.ext.isJarFile
import me.yangxiaobin.lib.ext.safeCopyTo
import me.yangxiaobin.lib.ext.touch
import java.io.File
import java.util.concurrent.Callable
import java.util.zip.Deflater


sealed interface TransformTicket {
    val from: File
    val to: File
    operator fun component1() = from
    operator fun component2() = to
}

data class ChangedFileTicket(override val from: File, override val to: File) : TransformTicket
data class DeleteTicket(override val from: File, override val to: File) : TransformTicket


interface TransformBus{

    fun takeTickets(tickets:List<TransformTicket>)
}


object TransformTicketImpl : TransformBus {


    override fun takeTickets(tickets: List<TransformTicket>) {

        tickets.forEach { t: TransformTicket ->

            val from = t.from

            when {
                from.isJarFile() -> TransformEngine.submitSync{ JarTransformer.transform(t) }
                from.isDirectory -> {

                    from.walkTopDown().forEach { childFile: File ->
                        if (childFile.isClassFile()) TransformEngine.submitSync { ClassTransformer.transform(t) }
                        else TransformEngine.submitSync { CopyTransformer.transform(t) }
                    }

                }
                from.isClassFile() -> TransformEngine.submitSync { ClassTransformer.transform(t) }
                else -> TransformEngine.submitSync{ CopyTransformer.transform(t) }
            }
        }


    }

}


object TransformEngine {

    private val executor: JUCExecutorService = InternalExecutor.fixed

    fun submitSync(runnable: Runnable) {
        /**
         * /Users/yangxiaobin/DevelopSpace/IDEA/gradle-plugin-template/samples/androidapp/build/intermediates/transforms/TestTransformV2/debug/1 (Is a directory)
        at me.yangxiaobin.lib.transform_v3.TransformEngine.submitSync(TransformAble.kt:69)
        at me.yangxiaobin.lib.transform_v3.TransformTicketImpl.takeTickets(TransformAble.kt:48)
        at me.yangxiaobin.lib.transform_v3.BaseTransformV3.dispatchInput(BaseTransformV3.kt:66)
        at me.yangxiaobin.lib.transform_v3.BaseTransformV3.transform(BaseTransformV3.kt:15)
        at com.test_a.TestATransformV3.transform(TestAPlugin.kt:28)
         */
        executor.submit(runnable).get()
    }

}


sealed interface FileTransformer {

    /**
     * 有可能是被删除的文件
     */
    fun transform(ticket: TransformTicket)
}

object CopyTransformer : FileTransformer {

    override fun transform(ticket: TransformTicket) {
        val (from, to) = ticket

        // Copy only is NOT deletion
        if (ticket !is DeleteTicket) from.safeCopyTo(to)
    }
}

object JarTransformer : FileTransformer {

    private val executor: JUCExecutorService = InternalExecutor.fixed

    override fun transform(ticket: TransformTicket) {

        val (input,output) = ticket

        val inputZipArchive = ZipArchive(input.toPath())
        val outputZipArchive = ZipArchive(output.toPath())

        val byteArrayList: List<Pair<ByteArray, String>> = inputZipArchive.listEntries()
            .map { entryName: String ->

                val bf = inputZipArchive.getContent(entryName)
                val bs = ByteArray(bf.remaining())
                bf.get(bs)

                Callable {
                    //TODO
                    //println("zip task entry :$entryName executed in thread :${Thread.currentThread().name}.")
                    TransformRegistry.getFoldConverter().transform(bs) } to entryName
            }
            .map { executor.submit(it.first).get() to it.second }


        executor.submit {
            byteArrayList.forEach { (bs, entryName) ->
                outputZipArchive.add(BytesSource(bs, entryName, Deflater.NO_COMPRESSION))
            }
        }.get()

        inputZipArchive.close()
        outputZipArchive.close()

        if (ticket is DeleteTicket) output.delete()
    }

}

object ClassTransformer : FileTransformer {

    override fun transform(ticket: TransformTicket) {

        val (input, output) = ticket

        val bs = TransformRegistry.getFoldConverter().transform(input.readBytes())

        if (ticket is DeleteTicket) output.delete()
        else output.touch().writeBytes(bs)
    }

}



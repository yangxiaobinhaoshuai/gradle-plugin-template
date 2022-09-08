package me.yangxiaobin.lib.transform

import com.android.zipflinger.BytesSource
import com.android.zipflinger.ZipArchive
import me.yangxiaobin.lib.*
import me.yangxiaobin.lib.executor.InternalExecutor
import me.yangxiaobin.lib.ext.safeCopyTo
import me.yangxiaobin.lib.ext.touch
import me.yangxiaobin.lib.log.InternalLogger
import me.yangxiaobin.lib.log.LogAware
import me.yangxiaobin.lib.log.LogDelegate
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.File
import java.util.concurrent.Executor
import java.util.zip.Deflater

private const val LOG_TAG = "Transformer"
private val defaultLogDelegate = LogDelegate(InternalLogger, LOG_TAG)

/**
 * 全量编译
 *
 * dirinput: /Users/yangxiaobin/DevelopSpace/IDEA/gradle-plugin-template/samples/androidapp/build/tmp/kotlin-classes/debug
 * file:     /Users/yangxiaobin/DevelopSpace/IDEA/gradle-plugin-template/samples/androidapp/build/tmp/kotlin-classes/debug/me/yangxiaobin/androidapp/MainActivityKt
 * .class
 *
 * out:      /Users/yangxiaobin/DevelopSpace/IDEA/gradle-plugin-template/samples/androidapp/build/intermediates/transforms/AbsLegacyTransform/debug/2
 * res:      /Users/yangxiaobin/DevelopSpace/IDEA/gradle-plugin-template/samples/androidapp/build/intermediates/transforms/AbsLegacyTransform/debug/2/me/yangxiaobin/androidapp/MainActivityKt.class
 *
 *
 * Empty class
 *          /Users/yangxiaobin/DevelopSpace/IDEA/gradle-plugin-template/samples/androidapp/build/intermediates/transforms/TestTransformV2/debug/1/me/yangxiaobin/androidapp/aspect/JavaAspect.class
 */
class ClassFileTypeTransformer(private val logDelegate: LogAware = defaultLogDelegate) : FileTypeTransformer,
    LogAware by logDelegate {

    override fun syncTransform(input: File, output: File) {

        //logI("class transformed from: $input into: $out.")

       /* val cr = ClassReader(input.readBytes())

        val cw = ClassWriter(ClassWriter.COMPUTE_MAXS)

        val cv = object : ClassVisitor(Opcodes.ASM7,cw) {

            override fun visit(
                version: Int,
                access: Int,
                name: String?,
                signature: String?,
                superName: String?,
                interfaces: Array<out String>?
            ) {
                super.visit(version, access, name, signature, superName, interfaces)
                //println("----> visit class :$name.")
            }
        }

        cr.accept(cv,  ClassReader.EXPAND_FRAMES)
        val bs = cw.toByteArray()

        output.touch().writeBytes(bs)*/

        val bs = TransformRegistry.getFoldConverter().transform(input.readBytes())
        output.touch().writeBytes(bs)

    }

}

/**
 * Android zipFlinger doc : https://android.googlesource.com/platform/tools/base/+/refs/heads/mirror-goog-studio-master-dev/zipflinger/
 */
class JarFileTypeTransformer(
    private val logDelegate: LogAware = defaultLogDelegate,
    private val executor: JUCExecutorService = InternalExecutor.fixed,
) : FileTypeTransformer, LogAware by logDelegate {

    override fun syncTransform(input: File, output: File) {

        //logI("jar transformed from: $input into: $output.")

        val inputZipArchive = ZipArchive(input.toPath())
        val outputZipArchive = ZipArchive(output.toPath())

        val byteArrayList: List<Pair<ByteArray, String>> = inputZipArchive.listEntries()
            .map { entryName ->

                val bf = inputZipArchive.getContent(entryName)
                val bs = ByteArray(bf.remaining())
                bf.get(bs)

                TypedTransformAction {
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
    }

}

class FileCopyTypeTransformer(private val logDelegate: LogAware = defaultLogDelegate) : FileTypeTransformer,
    LogAware by logDelegate {

    override fun syncTransform(input: File, output: File) {
        input.safeCopyTo(output)
        //.also { logI("copy from: $input into: $out.") }
    }

}

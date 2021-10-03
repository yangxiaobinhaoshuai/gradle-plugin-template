package me.yangxiaobin.sample.asm


import me.yangxiaobin.lib.asm.applyAsm
import me.yangxiaobin.lib.ext.currentWorkPath
import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.log.Logger
import java.io.File
import javax.swing.MenuElement


class AsmHelper {

//    private val logI: (String) -> Unit = AppLogger.log(LogLevel.INFO, TAG)

    companion object {

        private const val TAG = "AsmHelper"


        @JvmStatic
        fun main(args: Array<String>) {
            println("----> SampleJava main")

            Logger.setLevel(level = LogLevel.VERBOSE)

            val clazz: Class<AsmHelper> = AsmHelper::class.java

            val ins = try {
                val abc = this.javaClass.getResourceAsStream("/abc.txt")
                println("----> ${abc.bufferedReader().use { it.readText() }}")


                val ins = File("$currentWorkPath/app/ClazzStub.class").inputStream()

                //val inputAsString = ins.bufferedReader().use { it.readText() }
                //println("---> ins : $inputAsString")

                val byteArray = ins.applyAsm { SampleClassVisitor(it) }

                println("----> res size :${byteArray.size}")

                val cl = ByteArrayClassLoader(byteArray)

                val loadedClazz = cl.loadClass("me.yangxiaobin.sample.asm.ClazzStub")
                val instance: Any = loadedClazz.newInstance()

                println("----> instance type :${instance.javaClass.methods.map { it.name }}")

                val method = instance.javaClass.getMethod("show")

                method.invoke(instance)

            } catch (e: Exception) {
                e.printStackTrace()
                println("----> e :$e")
            }

        }
    }
}

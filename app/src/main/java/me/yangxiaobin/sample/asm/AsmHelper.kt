package me.yangxiaobin.sample.asm


import me.yangxiaobin.lib.asm.applyAsm
import java.io.InputStream


class AsmHelper {

//    private val logI: (String) -> Unit = AppLogger.log(LogLevel.INFO, TAG)

    companion object {

        private const val TAG = "AsmHelper"


        @JvmStatic
        fun main(args: Array<String>) {
            println("----> SampleJava main")

            val clazz: Class<ClazzStub> = ClazzStub::class.java

            val ins = try {
                val ins: InputStream = clazz.getResourceAsStream("ClazzStub.class")
                //val inputAsString = ins.bufferedReader().use { it.readText() }
                //println("---> ins : $inputAsString")

                val byteArray = ins.applyAsm { SampleClassVisitor(it) }

                println("----> res :${String(byteArray)}")

            } catch (e: Exception) {
                e.printStackTrace()
                println("----> e :$e")
            }

        }
    }
}

package me.yangxiaobin.graph

import me.yangxiaobin.graph.usecase.QuickFind

class UnionFindTest {
    companion object {
        private fun log(message: String) = println(message)

        @JvmStatic
        fun main(args: Array<String>) {
            log("uf test ----> ")

            val qf = QuickFind<Int>()

            qf.union(0,1)
            qf.union(0,2)
            qf.union(0,3)
            qf.union(0,4)

            qf.union(5,6)
            qf.union(5,7)
            qf.union(5,8)
            qf.union(5,9)

            log("""
                union find result : ${qf.isConnected(9,16)}
            """.trimIndent())

        }

    }
}

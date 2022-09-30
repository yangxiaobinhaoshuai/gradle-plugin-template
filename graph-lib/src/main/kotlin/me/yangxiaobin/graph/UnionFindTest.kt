package me.yangxiaobin.graph

import me.yangxiaobin.graph.usecase.QuickFind
import me.yangxiaobin.graph.usecase.QuickUnion

class UnionFindTest {
    companion object {
        private fun log(message: String) = println(message)

        @JvmStatic
        fun main(args: Array<String>) {

            log("union find  test ----> see below ")

            val qf = QuickFind<Int>()

            qf.union(0,1)
            qf.union(0,2)
            qf.union(0,3)
            qf.union(0,4)

            qf.union(5,6)
            qf.union(5,7)
            qf.union(5,8)
            qf.union(5,9)

            val qu = QuickUnion<Int>()

            qu.union(0,1)
            qu.union(0,2)
            qu.union(0,3)
            qu.union(0,4)

            qu.union(2,5)
            qu.union(3,6)
            qu.union(5,7)

            qu.union(8,9)
            qu.union(8,10)
            qu.union(9,11)

//            log("""
//                union find result : ${qf.isConnected(9,16)}
//            """.trimIndent())

            log(
                """
                union find cc :${qu.count()}
                isConnected : ${qu.isConnected(11, 10)}
            """.trimIndent()
            )

        }

    }
}

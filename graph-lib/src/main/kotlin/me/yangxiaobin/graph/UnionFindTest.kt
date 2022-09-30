package me.yangxiaobin.graph

import me.yangxiaobin.graph.usecase.QuickFind
import me.yangxiaobin.graph.usecase.QuickUnion
import me.yangxiaobin.graph.usecase.WeightedQuickUnion

class UnionFindTest {
    companion object {
        private fun log(message: String) = println(message)

        @JvmStatic
        fun main(args: Array<String>) {

            log("union find  test ----> see below\r\n")

            val qf = QuickFind<Int>()

            qf.union(0,1)
            qf.union(0,2)
            qf.union(0,3)
            qf.union(0,4)

            qf.union(2,5)
            qf.union(3,6)
            qf.union(5,7)

            qf.union(8,9)
            qf.union(8,10)
            qf.union(9,11)

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


            val wqu = WeightedQuickUnion<Int>()

            wqu.union(0,1)
            wqu.union(0,2)
            wqu.union(0,3)
            wqu.union(0,4)

            wqu.union(2,5)
            wqu.union(3,6)
            wqu.union(5,7)

            wqu.union(8,9)
            wqu.union(8,10)
            wqu.union(9,11)


//            log("""
//                quick find result
//                count: ${qf.count()}
//                is connected : ${qf.isConnected(7,6)}
//            """.trimIndent())

            log(
                """
                quick union result
                cc :${qu.count()}
                isConnected : ${qu.isConnected(7, 6)}
            """.trimIndent()
            )

//            log(
//                """
//                weighted quick union result
//                cc :${wqu.count()}
//                isConnected : ${wqu.isConnected(7, 6)}
//            """.trimIndent()
//            )

            log("\r\nunion find  test ----> end ")
        }

    }
}

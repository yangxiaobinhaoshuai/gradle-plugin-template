package me.yangxiaobin.graph.test

import me.yangxiaobin.graph.SimpleGraph
import me.yangxiaobin.graph.SimpleGraphImpl
import me.yangxiaobin.graph.toAdjString

fun graphLog(message: String) = println(message)

class GraphTest {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            graphLog("graph test ==> ")

            val simpleGraph: SimpleGraph<Int> = SimpleGraphImpl<Int>(64)

            simpleGraph
                .addVertexPair(0, 1)
                .addVertexPair(0, 2)
                .addVertexPair(0, 3)
                .addVertexPair(0, 4)

                .addVertexPair(5, 6)
                .addVertexPair(5, 7)
                .addVertexPair(5, 8)
                .addVertexPair(5, 9)


            graphLog("graph toAdjString: ${simpleGraph.toAdjString()}")
        }
    }
}

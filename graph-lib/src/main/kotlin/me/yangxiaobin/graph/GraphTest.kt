package me.yangxiaobin.graph

import me.yangxiaobin.graph.usecase.Search

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


            val search = Search(simpleGraph, 4.toSimpleVertex())

            graphLog("graph toAdjString: ${simpleGraph.toAdjString()}")

            graphLog(
                """
                search result : 
                isMarked :${search.isConnected(5.toSimpleVertex())}
                count :${search.getConnectedCount()}
            """.trimIndent()
            )
        }
    }
}

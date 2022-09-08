package me.yangxiaobin.graph


interface Vertex<DATA> {

    val data: DATA

    operator fun plus(other: Vertex<DATA>): Edge<Vertex<DATA>>

    override fun hashCode(): Int

    override fun equals(other: Any?): Boolean
}

interface Edge<VERTEX> {

    val first: VERTEX

    val second: VERTEX

    override fun hashCode(): Int

    override fun equals(other: Any?): Boolean
}

/**
 * 邻接表表示
 */
interface Graph<VERTEX, EDGE> {

    fun addEdge(edge: EDGE)

    fun adjacentVertexes(vertex: VERTEX): Iterable<VERTEX>

    fun removeVertex(vertex: VERTEX)

    /**
     * 邻接表的字符串表示
     */
    fun toAdjString() = this.toString()
}




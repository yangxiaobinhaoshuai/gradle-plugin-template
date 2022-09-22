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
    operator fun component1(): VERTEX = first
    operator fun component2(): VERTEX = second
}

interface Graph<VERTEX, EDGE> : Iterable<VERTEX> {


    /**
     *顶点数
     */
    fun v(): Int

    /**
     * 边数
     */
    fun e(): Int

    fun addEdge(edge: EDGE)

    fun adjacentVertexes(vertex: VERTEX): Iterable<VERTEX>

    fun removeVertex(vertex: VERTEX)
}




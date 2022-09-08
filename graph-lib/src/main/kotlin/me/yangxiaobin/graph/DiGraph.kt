package me.yangxiaobin.graph


interface DiVertex<D> : Vertex<D> {

    val pointers: Iterable<DiVertex<D>>
}

interface DiEdge<V> : Edge<V> {

    val front: V
    val end: V

    override val first: V get() = front
    override val second: V get() = end
}

interface DiGraph<V, E> : Graph<V, E> {


    fun inAdjVertexes(vertex: V): Iterable<V>

    fun outAdjVertexes(vertex: V): Iterable<V>
}

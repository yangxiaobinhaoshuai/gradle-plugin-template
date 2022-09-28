package me.yangxiaobin.graph


data class SimpleVertex<T>(override val data: T) : Vertex<T> {
    override fun plus(other: Vertex<T>): Edge<Vertex<T>> = SimpleEdge(this, other)

    override fun toString(): String = data.toString()
    override fun equals(other: Any?): Boolean = when (other) {
        null -> false
        !is SimpleVertex<*> -> false
        else -> this.data == other.data
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }
}

fun <T> T.toSimpleVertex(): SimpleVertex<T> = SimpleVertex(this)


data class SimpleEdge<V>(override val first: V, override val second: V) : Edge<V>


interface SimpleGraph<T> : Graph<SimpleVertex<T>, Edge<SimpleVertex<T>>> {
    fun addVertexPair(t1: T, t2: T): SimpleGraph<T> = apply {

        val v1: SimpleVertex<T> = t1.toSimpleVertex()
        val v2: SimpleVertex<T> = t2.toSimpleVertex()

        val e: Edge<Vertex<T>> = v1 + v2
        @Suppress("UNCHECKED_CAST")
        this.addEdge(e as Edge<SimpleVertex<T>>)
    }

}

open class SimpleGraphImpl<T>(initialSize: Int = 128) : SimpleGraph<T>, GraphImpl<SimpleVertex<T>,
        Edge<SimpleVertex<T>>>(initialSize) {
}

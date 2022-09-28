package me.yangxiaobin.graph.usecase

import me.yangxiaobin.graph.Graph
import java.util.*

class BfsPath<V, E>(graph: Graph<V, E>, target: V) {

    private val marked = mutableMapOf<V, Boolean>()

    private val parentTree = mutableMapOf<V, V>()


    init {
        bfs(graph, target)
    }


    private fun bfs(graph: Graph<V, E>, target: V) {

        val queue: Queue<V> = LinkedList<V>()

        marked[target] = true

        queue.add(target)

        while (queue.isNotEmpty()) {

            val next = queue.poll()

            for (v in graph.getAdjacentVertexes(next)) {
                if (marked[v] != true) {
                    parentTree[v] = next
                    marked[v] = true
                    queue.add(v)
                }
            }

        }
    }

    fun hasPathTo(v: V): Boolean = marked[v] == true


    fun pathTo(v: V): Iterable<V> {

        if (!hasPathTo(v)) return emptyList()

        val actualPath = mutableListOf<V>()

        var parent: V? = parentTree[v]

        while (parent != null) {
            actualPath += parent
            parent = parentTree[parent]
        }

        if (actualPath.isNotEmpty()) actualPath.add(0, v)

        return actualPath.reversed()
    }


}

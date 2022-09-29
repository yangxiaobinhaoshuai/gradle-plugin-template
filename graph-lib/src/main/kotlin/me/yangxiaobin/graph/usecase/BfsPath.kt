package me.yangxiaobin.graph.usecase

import me.yangxiaobin.graph.SimpleGraph
import java.util.*

class BfsPath<T>(graph: SimpleGraph<T>, target: T) {

    private val marked = mutableMapOf<T, Boolean>()

    private val parentTree = mutableMapOf<T, T>()


    init {
        bfs(graph, target)
    }


    private fun bfs(graph: SimpleGraph<T>, target: T) {

        val queue: Queue<T> = LinkedList<T>()

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

    fun hasPathTo(v: T): Boolean = marked[v] == true


    fun pathTo(v: T): Iterable<T> {

        if (!hasPathTo(v)) return emptyList()

        val actualPath = mutableListOf<T>()

        var parent: T? = parentTree[v]

        while (parent != null) {
            actualPath += parent
            parent = parentTree[parent]
        }

        if (actualPath.isNotEmpty()) actualPath.add(0, v)

        return actualPath.reversed()
    }


}

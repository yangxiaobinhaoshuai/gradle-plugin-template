package me.yangxiaobin.graph.usecase

import me.yangxiaobin.graph.SimpleGraph


class DfsPath<T>(graph: SimpleGraph<T>, target: T) {

    private val marked = mutableMapOf<T, Boolean>()

    /**
     * this to parent
     */
    private val parentTree = mutableMapOf<T, T>()

    init {
        dfs(graph, target)
    }

    private fun dfs(graph: SimpleGraph<T>, target: T) {
        marked[target] = true

        for (v in graph.getAdjacentVertexes(target)) {
            if (marked[v] != true) {
                parentTree[v] = target
                dfs(graph, v)
            }
        }
    }

    /**
     * 两点间是否存在路径
     */
    fun hasPathTo(v: T): Boolean = marked.getOrDefault(v, false)

    /**
     * 获取两个顶点的路径
     */
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

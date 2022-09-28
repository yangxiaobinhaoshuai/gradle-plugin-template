package me.yangxiaobin.graph.usecase

import me.yangxiaobin.graph.Graph


class Path<V, E>(graph: Graph<V, E>, target: V) {

    private val marked = mutableMapOf<V, Boolean>()

    /**
     * this to parent
     */
    private val parentMap = mutableMapOf<V,V>()

    init {
        dfs(graph, target)
    }

    private fun dfs(graph: Graph<V, E>, target: V) {
        marked[target] = true

        for (v in graph.getAdjacentVertexes(target)) {
            if (marked[v] != true) {
                parentMap[v] = target
                dfs(graph, v)
            }
        }
    }

    /**
     * 两点间是否存在路径
     */
    fun hasPathTo(v: V): Boolean = marked.getOrDefault(v, false)

    /**
     * 获取两个顶点的路径
     */
    fun pathTo(v: V): Iterable<V> {
        if (!hasPathTo(v)) return emptyList()

        val actualPath = mutableListOf<V>()

        var parent:V? = parentMap[v]

        while (parent != null) {
            actualPath += parent
            parent = parentMap[parent]
        }

        if (actualPath.isNotEmpty()) actualPath.add(0,v)

        return actualPath.reversed()
    }

}

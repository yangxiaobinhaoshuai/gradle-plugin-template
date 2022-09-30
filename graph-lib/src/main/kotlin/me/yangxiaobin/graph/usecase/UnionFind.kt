package me.yangxiaobin.graph.usecase

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger


/**
 * 解决连通性问题
 * 优势 : 检查两点连通 和 添加一条边有效率
 *
 * 动态算法: 任何时候接近常数时间内，判断两点连通性或者完成大量连通性查询和插入混合操作
 */
interface UnionFind<T> {
    /**
     * 添加一条连接
     */
    fun union(t1: T, t2: T)

    /**
     * 所在连通分量标识符
     */
    fun find(t: T): Int


    /**
     * 判断两点是否处于同一分量
     */
    fun isConnected(t1: T, t2: T): Boolean

    /**
     * 连通分量的总数量
     */
    fun count(): Int
}


/**
 * 特点：
 * find 效率很高, Union 复杂度很高
 *
 * 对于规模为 n 的元素集合，时间复杂度为 ：O(n)
 */
class QuickFind<T> : UnionFind<T> {

    /**
     * 分量 ids
     */
    private val idMap: MutableMap<T, Int> = ConcurrentHashMap<T, Int>()

    /**
     * Connected component identify generator
     */
    private val ccIdGenerator = AtomicInteger(0)

    /**
     * 分量数量
     */
    private var ccCount = 0

    /**
     * 归并 t1 和 t2 到同一个分量
     *
     * 将 t2 的 index 改为 t1 的 index
     *
     * O(1)
     */
    override fun union(t1: T, t2: T) {

        var id1 = find(t1)
        var id2 = find(t2)

        when {

            id1 < 0 -> {
                id2 = idMap.getOrPut(t2) { ccCount++; ccIdGenerator.incrementAndGet() }
                idMap[t1] = id2
            }

            id2 < 0 -> {
                id1 = idMap.getOrPut(t1) { ccCount++; ccIdGenerator.incrementAndGet() }
                idMap[t2] = id1
            }

            id1 == id2 -> return

            else -> {
                id2 = idMap.getValue(t2)
                idMap[t1] = id2
                ccCount--
            }

        }
    }

    override fun find(t: T): Int = idMap.getOrDefault(t, -1)

    override fun isConnected(t1: T, t2: T): Boolean {

        val p1 = find(t1)
        val p2 = find(t2)

        return if (p1 == -1 || p2 == -1)
            false
        else
            p1 == p2
    }

    override fun count(): Int = ccCount

}


/**
 * 目的为了提高 Union 的效率
 * 与 Quick-Find 算法互补
 * 使用父节点树
 *
 * O(1)
 */
class QuickUnion<T>(mapInitialSize: Int = 64) : UnionFind<T> {
    /**
     * 分量 ids
     */
    private val parentTree: MutableMap<T, T> = ConcurrentHashMap<T, T>(mapInitialSize)

    /**
     * 分量数量
     */
    private val idMap: MutableMap<T, Int> = ConcurrentHashMap<T, Int>()

    private val count = AtomicInteger(0)

    /**
     * t2 的父节点 = t1 的父节点
     *
     * O(1)
     */
    override fun union(t1: T, t2: T) {

        val p1 = find(t1)
        val p2 = find(t2)

        if (p1 >= 0 && p2 >= 0 && p1 == p2) return

        if (parentTree[t1] == null) {
            idMap.computeIfAbsent(t1) { count.incrementAndGet() }
        }

        parentTree[t2] = t1
    }

    override fun find(t: T): Int {

        var parent: T? = parentTree[t]
        while (parent != null && parentTree[parent] != null) parent = parentTree[parent]

        return if (parent == null) -1 else idMap.getOrDefault(parent, -1)
    }

    override fun isConnected(t1: T, t2: T): Boolean {
        val p1 = find(t1)
        val p2 = find(t2)

        //println("---> p1:$p1, p2:$p2 , $parentTree")

        return when {
            p1 == -1 || p2 == -1 -> false
            else -> p1 == p2
        }
    }

    override fun count(): Int = count.get()
}

/**
 * Base on quick-union
 *
 * O(logn)
 */
class WeightedQuickUnion<T>(mapInitialSize: Int = 64) : UnionFind<T> {

    private val parentTree: MutableMap<T, T> = ConcurrentHashMap<T, T>(mapInitialSize)

    private val idMap: MutableMap<T, Int> = ConcurrentHashMap<T, Int>()

    private val count = AtomicInteger()

    /**
     * 各个连通分量的大小
     */
    private val sizeMap: MutableMap<Int, Int> = ConcurrentHashMap<Int, Int>()

    override fun union(t1: T, t2: T) {

        val p1 = find(t1)
        val p2 = find(t2)

        if (p1 >= 0 && p2 >= 0 && p1 == p2) return

        val id1 = idMap.getOrDefault(t1, -1)
        val id2 = idMap.getOrDefault(t2, -1)

        val s1 = sizeMap.getOrDefault(id1, 0)
        val s2 = sizeMap.getOrDefault(id2, 0)

        if (s1 > s2) {


        } else {

        }

        if (parentTree[t1] == null) {
            idMap.computeIfAbsent(t1) { count.incrementAndGet() }
            sizeMap[count.get()] = 1
        }


    }

    override fun find(t: T): Int {

        var parent = parentTree[t]
        while (parent != null && parentTree[parent] != null) parent = parentTree[parent]

        return if (parent == null) -1 else idMap.getOrDefault(parent, -1)
    }

    override fun isConnected(t1: T, t2: T): Boolean {
        val p1 = find(t1)
        val p2 = find(t2)
        return when {
            p1 == -1 || p2 == -1 -> false
            else -> p1 == p2
        }
    }

    override fun count(): Int = count.get()

}

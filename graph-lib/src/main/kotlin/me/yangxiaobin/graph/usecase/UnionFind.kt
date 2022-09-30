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
    private var ccCount = AtomicInteger()

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
                id2 = idMap.getOrPut(t2) { ccCount.incrementAndGet(); ccIdGenerator.incrementAndGet() }
                idMap[t1] = id2
            }

            id2 < 0 -> {
                id1 = idMap.getOrPut(t1) { ccCount.incrementAndGet(); ccIdGenerator.incrementAndGet() }
                idMap[t2] = id1
            }

            id1 == id2 -> return

            else -> {
                id2 = idMap.getValue(t2)
                idMap[t1] = id2
                ccCount.decrementAndGet()
            }

        }
    }

    override fun find(t: T): Int = idMap.getOrDefault(t, -1)

    override fun isConnected(t1: T, t2: T): Boolean {

        val id1 = find(t1)
        val id2 = find(t2)

        return if (id1 == -1 || id2 == -1)
            false
        else
            id1 == id2
    }

    override fun count(): Int = ccCount.get()

}


/**
 * 目的为了提高 Union 的效率
 * 与 Quick-Find 算法互补
 * 使用父节点树
 *
 * O(1)
 */
class QuickUnion<T> : UnionFind<T> {

    /**
     * 分量 ids
     */
    private val parentTree: MutableMap<T, T> = ConcurrentHashMap<T, T>()

    /**
     * 分量数量
     */
    private val idMap: MutableMap<T, Int> = ConcurrentHashMap<T, Int>()


    private val ccIdGenerator = AtomicInteger()

    private var ccCount = AtomicInteger()

    /**
     * t2 的父节点 = t1 的父节点
     *
     * O(1)
     */
    override fun union(t1: T, t2: T) {

        var id1 = find(t1)
        val id2 = find(t2)

        when {
            id1 < 0 && id2 < 0 -> {
                // t2 -> t1
                parentTree[t2] = t1
                id1 = idMap.getOrPut(t1) { ccCount.incrementAndGet(); ccIdGenerator.incrementAndGet() }
                idMap[t1] = id1
            }

            id1 < 0 -> {
                val top2Parent: T = getTopParent(t2) ?: t2
                parentTree[t1] = top2Parent
            }

            id2 < 0 -> {
                val top1Parent: T = getTopParent(t1) ?: t1
                parentTree[t2] = top1Parent
            }

            id1 == id2 -> return

            else -> {
                val top1Parent: T = getTopParent(t1) ?: t1
                val top2Parent: T = getTopParent(t2) ?: t2

                parentTree[top1Parent] = top2Parent

                ccCount.decrementAndGet()
            }
        }
    }

    /**
     * O(n)
     */
    private fun getTopParent(t: T): T? {
        var p = parentTree[t]
        while (p != null && parentTree[p] != null) p = parentTree[p]
        return p
    }

    override fun find(t: T): Int {

        val parent = getTopParent(t)

        return if (parent == null)
            -1
        else
            idMap.getOrDefault(parent, -1)
    }

    override fun isConnected(t1: T, t2: T): Boolean {

        val id1 = find(t1)
        val id2 = find(t2)

        return if (id1 == -1 || id2 == -1)
            false
        else
            id1 == id2
    }

    override fun count(): Int = ccCount.get()
}

/**
 * Base on quick-union
 *
 * O(logn)
 */
class WeightedQuickUnion<T> : UnionFind<T> {

    private val parentTree: MutableMap<T, T> = ConcurrentHashMap<T, T>()

    private val idMap: MutableMap<T, Int> = ConcurrentHashMap<T, Int>()

    private val ccIdGenerator = AtomicInteger()

    private var ccCount = AtomicInteger()

    /**
     * 各个连通分量的大小
     */
    private val sizeMap: MutableMap<Int, Int> = ConcurrentHashMap<Int, Int>()

    override fun union(t1: T, t2: T) {

        var id1 = find(t1)
        val id2 = find(t2)

        when {
            id1 < 0 && id2 < 0 -> {
                parentTree[t2] = t1
                id1 = idMap.getOrPut(t1) { ccCount.incrementAndGet(); ccIdGenerator.incrementAndGet() }
                idMap[t1] = id1

                sizeMap[id1] = sizeMap.getOrDefault(id1, 0) + 1
            }

            id1 < 0 -> {
                val top2Parent: T = getTopParent(t2) ?: t2
                parentTree[t1] = top2Parent

                sizeMap[id1] = sizeMap.getOrDefault(id1, 0) + 1
            }

            id2 < 0 -> {
                val top1Parent: T = getTopParent(t1) ?: t1
                parentTree[t2] = top1Parent

                sizeMap[id2] = sizeMap.getOrDefault(id2, 0) + 1
            }

            id1 == id2 -> return

            else -> {

                val s1 = sizeMap.getOrDefault(id1,0)
                val s2 = sizeMap.getOrDefault(id2,0)

                val top1Parent: T = getTopParent(t1) ?: t1
                val top2Parent: T = getTopParent(t2) ?: t2

                if (s1 > s2) {
                    parentTree[top2Parent] = top1Parent
                    sizeMap[id1] = sizeMap.getOrDefault(id1,0) + sizeMap.getOrDefault(id2,0)
                } else {
                    parentTree[top1Parent] = top2Parent
                    sizeMap[id2] = sizeMap.getOrDefault(id1,0) + sizeMap.getOrDefault(id2,0)
                }

                ccCount.decrementAndGet()
            }

        }

    }

    private fun getTopParent(t: T): T? {
        var p = parentTree[t]
        while (p != null && parentTree[p] != null) p = parentTree[p]
        return p
    }

    override fun find(t: T): Int {

        val parent = getTopParent(t)

        return if (parent == null)
            -1
        else
            idMap.getOrDefault(parent, -1)
    }

    override fun isConnected(t1: T, t2: T): Boolean {
        val id1 = find(t1)
        val id2 = find(t2)
        return if (id1 < 0 || id2 < 0)
            false
        else
            id1 == id2
    }

    override fun count(): Int = ccCount.get()

}

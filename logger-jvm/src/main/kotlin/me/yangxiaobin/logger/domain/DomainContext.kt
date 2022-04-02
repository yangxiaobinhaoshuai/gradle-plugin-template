package me.yangxiaobin.logger.domain

/**
 * An indexed set.
 * Inspired by [CoroutineContext]
 */
interface DomainContext {

    fun <R> fold(initial: R, operation: (R, DomainElement) -> R): R

    operator fun <E : DomainElement> get(key: Key<E>): E?

    fun minusKey(removedKey: Key<*>): DomainContext

    operator fun plus(other: DomainContext): DomainContext {
        if (other === EmptyDomainContext) return this

        return other.fold(this) { acc: DomainContext, element: DomainElement ->

            val retained: DomainContext = acc.minusKey(element.key)

            if (retained === EmptyDomainContext) element
            else CombinedDomainContext(retained, element)
        }
    }


    /**
     * @see CombinedContext.toString()
     */
    fun dump(): String =
        "[" + fold("") { acc, element ->
            if (acc.isEmpty()) element.toString() else "$acc, $element"
        } + "]"
}

interface DomainElement : DomainContext {

    val key: Key<*>

    override fun <E : DomainElement> get(key: Key<E>): E? =
        @Suppress("UNCHECKED_CAST")
        if (this.key == key) this as E else null

    override fun <R> fold(initial: R, operation: (R, DomainElement) -> R): R = operation(initial, this)

    /**
     * Return a [DomainContext] without [DomainElement] with the specific key.
     */
    override fun minusKey(removedKey: Key<*>): DomainContext {
        return if (this.key == removedKey) EmptyDomainContext else this
    }
}


interface Key<E : DomainElement>

internal class CombinedDomainContext(
    private val left: DomainContext,
    private val right: DomainElement
) : DomainContext {

    override fun <E : DomainElement> get(key: Key<E>): E? {
        return right[key] ?: left[key]
    }

    override fun minusKey(removedKey: Key<*>): DomainContext {
        val leftRetained by lazy { left.minusKey(removedKey) }
        return when {
            right[removedKey] != null -> left
            leftRetained === left -> this
            leftRetained === EmptyDomainContext -> right
            else -> CombinedDomainContext(leftRetained, right)
        }
    }

    override fun <R> fold(initial: R, operation: (R, DomainElement) -> R): R =
        operation(left.fold(initial, operation), right)

    override fun toString(): String =
        "[" + fold("") { acc, element ->
            if (acc.isEmpty()) element.toString() else "$acc, $element"
        } + "]"
}

object EmptyDomainContext : DomainContext {

    override fun <R> fold(initial: R, operation: (R, DomainElement) -> R): R = initial

    override fun <E : DomainElement> get(key: Key<E>): E? = null

    override fun minusKey(removedKey: Key<*>): DomainContext = this

    override fun plus(other: DomainContext): DomainContext = other
}

abstract class AbsDomainElement(override val key: Key<*>) : DomainElement

abstract class AbsKey<E : DomainElement> : Key<E>



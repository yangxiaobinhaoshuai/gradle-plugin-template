package me.yangxiaobin.logger.uitlity

import me.yangxiaobin.logger.domain.DomainElement

interface DomainElementInterceptor {

    fun wantIntercept(element: DomainElement?): Boolean

    fun transform(element: DomainElement?): DomainElement?

    operator fun plus(other: DomainElementInterceptor): DomainElementInterceptor {
        return object : DomainElementInterceptor {
            override fun wantIntercept(element: DomainElement?): Boolean =
                this@DomainElementInterceptor.wantIntercept(element) or other.wantIntercept(element)

            override fun transform(element: DomainElement?): DomainElement? {
                val first: DomainElement? = if(this.wantIntercept(element)) this@DomainElementInterceptor.transform(element) else element
                val second: DomainElement? = if (other.wantIntercept(element)) other.transform(first) else first
                return second
            }

        }
    }
}
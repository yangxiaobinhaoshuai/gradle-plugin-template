package me.yangxiaobin.logger.elements

import me.yangxiaobin.logger.uitlity.DomainElementInterceptor
import me.yangxiaobin.logger.domain.AbsDomainElement
import me.yangxiaobin.logger.domain.AbsKey

data class InterceptorLogElement(val interceptor: DomainElementInterceptor) : AbsDomainElement(InterceptorLogElement) {

    companion object Key : AbsKey<InterceptorLogElement>()
}

package me.yangxiaobin.logger.elements

import me.yangxiaobin.logger.uitlity.Interceptor
import me.yangxiaobin.logger.domain.AbsDomainElement
import me.yangxiaobin.logger.domain.AbsKey

data class InterceptorLogElement(val interceptor: Interceptor) : AbsDomainElement(InterceptorLogElement) {

    companion object Key : AbsKey<InterceptorLogElement>()
}

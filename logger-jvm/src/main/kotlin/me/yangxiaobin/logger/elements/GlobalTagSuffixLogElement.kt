package me.yangxiaobin.logger.elements

import me.yangxiaobin.logger.domain.AbsKey
import me.yangxiaobin.logger.domain.AbsDomainElement

data class GlobalTagSuffixLogElement(val tagSuffix: String) : AbsDomainElement(GlobalTagSuffixLogElement) {

    companion object Key : AbsKey<GlobalTagSuffixLogElement>()
}

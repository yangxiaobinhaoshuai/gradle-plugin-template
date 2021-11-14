package me.yangxiaobin.logger.elements

import me.yangxiaobin.logger.domain.AbsKey
import me.yangxiaobin.logger.domain.AbsDomainElement
import me.yangxiaobin.logger.uitlity.Formatter

data class FormatLogElement(val formatter: Formatter) : AbsDomainElement(FormatLogElement) {

    companion object Key : AbsKey<FormatLogElement>()
}

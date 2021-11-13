package me.yangxiaobin.logger.elements

import me.yangxiaobin.logger.AbsKey
import me.yangxiaobin.logger.AbsLogElement
import me.yangxiaobin.logger.Formatter

data class FormatLogElement(val formatter: Formatter) : AbsLogElement(FormatLogElement) {
    companion object Key : AbsKey<FormatLogElement>()
}

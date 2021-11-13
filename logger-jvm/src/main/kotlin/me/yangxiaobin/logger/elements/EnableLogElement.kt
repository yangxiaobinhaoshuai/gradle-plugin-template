package me.yangxiaobin.logger.elements

import me.yangxiaobin.logger.AbsKey
import me.yangxiaobin.logger.AbsLogElement

data class EnableLogElement(val enable: Boolean) : AbsLogElement(EnableLogElement) {

    companion object Key : AbsKey<EnableLogElement>()

}

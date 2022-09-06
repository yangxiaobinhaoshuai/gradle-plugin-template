package me.yangxiaobin.lib.ext


fun <T> T.toIf(condition: Boolean, transform: (T) -> T): T = if (condition) transform(this) else this

val Any?.neatName get() = this?.javaClass?.simpleName ?: "NULL"

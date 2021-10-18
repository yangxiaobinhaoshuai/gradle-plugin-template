package me.yangxiaobin.lib.ext


fun <T> T.transformIf(condition: Boolean, transform: (T) -> T) = if (condition) transform(this) else this

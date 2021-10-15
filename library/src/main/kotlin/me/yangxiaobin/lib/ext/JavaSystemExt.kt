package me.yangxiaobin.lib.ext


val currentWorkPath: String = System.getProperty("user.dir")

val currentUserHomePath: String = System.getProperty("user.home")

val currentJreClasspath: String? = System.getProperty("sun.boot.class.path")

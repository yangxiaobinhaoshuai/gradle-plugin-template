package me.yangxiaobin.lib.ext

import java.lang.management.ManagementFactory


val currentWorkPath: String = System.getProperty("user.dir")

val currentUserHomePath: String = System.getProperty("user.home")

val currentJreClasspath: String? = System.getProperty("sun.boot.class.path")

val currentProcessId: Long = ManagementFactory.getRuntimeMXBean().name?.substringBefore("@")?.toLongOrNull() ?: -1L

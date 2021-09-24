package me.yangxiaobin.lib.ext

import org.gradle.api.Project


fun Project.getProjectProp(key: String): String? = this.gradle.startParameter.projectProperties[key]

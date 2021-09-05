package me.yangxiaobin.base_lib

import org.gradle.api.Project


fun Project.getProjectProp(key: String): String? = this.gradle.startParameter.projectProperties[key]
package me.yangxiaobin.base_lib

import org.gradle.api.Project


fun Project.getProjectProp1(key: String): String? = this.gradle.startParameter.projectProperties[key]
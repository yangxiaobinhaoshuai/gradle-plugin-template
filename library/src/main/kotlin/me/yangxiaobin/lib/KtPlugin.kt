package me.yangxiaobin.lib

import me.yangxiaobin.base_lib.Logger
import org.gradle.api.Plugin
import org.gradle.api.Project

class KtPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        Logger.tag(TAG)
        Logger.d("KtPlugin applied.")
    }

    private companion object {
        private const val TAG = "KtPlugin"
    }

}
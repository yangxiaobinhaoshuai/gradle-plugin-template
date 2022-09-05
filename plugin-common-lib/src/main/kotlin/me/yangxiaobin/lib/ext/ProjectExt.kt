package me.yangxiaobin.lib.ext

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import me.yangxiaobin.lib.annotation.AfterEvaluation
import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.log.InternalLogger
import me.yangxiaobin.lib.log.log
import org.gradle.api.Project
import org.gradle.api.internal.tasks.DefaultGroovySourceSet
import org.gradle.api.plugins.Convention
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.internal.extensibility.DefaultExtraPropertiesExtension
import org.jetbrains.kotlin.gradle.plugin.sources.DefaultKotlinSourceSet
import java.io.File

private val projectLogger = InternalLogger.copy()
private val logV = projectLogger.log(LogLevel.VERBOSE,"lib.project.ext")

val Project.mainSourceSet: SourceSet?
    get() = (this.properties["sourceSets"] as SourceSetContainer).findByName("main")

@AfterEvaluation
val Project.getAppExtension: AppExtension?
    get() = this.extensions.getByName("android") as? AppExtension

@AfterEvaluation
val Project.getAppPlugin: AppPlugin?
    get() = this.plugins.findPlugin(AppPlugin::class.java)

@AfterEvaluation
val Project.getExtExtension: DefaultExtraPropertiesExtension?
    get() = this.extensions.getByName("ext") as? DefaultExtraPropertiesExtension

fun Project.getProjectProp(key: String): String? = this.gradle.startParameter.projectProperties[key]


enum class SourceLanguage { JAVA, KOTLIN, GROOVY }

@AfterEvaluation
fun Project.getSourceSetDirs(language: SourceLanguage): List<File> {

    val mainSourceSet = this.mainSourceSet
    requireNotNull(mainSourceSet) { logV("mainSourceSet is null, so sourceDir returned empty");return emptyList() }

    return when (language) {
        SourceLanguage.JAVA -> mainSourceSet.java.srcDirs
        SourceLanguage.KOTLIN -> ((mainSourceSet.extensions as Convention).plugins["kotlin"] as DefaultKotlinSourceSet).kotlin.srcDirs
        SourceLanguage.GROOVY -> ((mainSourceSet.extensions as Convention).plugins["groovy"] as DefaultGroovySourceSet).groovy.srcDirs
    }.filter { it.exists() }
}

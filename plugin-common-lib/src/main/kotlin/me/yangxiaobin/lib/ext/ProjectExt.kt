package me.yangxiaobin.lib.ext

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import me.yangxiaobin.lib.annotation.AfterEvaluation
import org.gradle.api.Project
import org.gradle.api.internal.tasks.DefaultGroovySourceSet
import org.gradle.api.plugins.Convention
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.internal.extensibility.DefaultExtraPropertiesExtension
import org.jetbrains.kotlin.gradle.plugin.sources.DefaultKotlinSourceSet
import java.io.File

/**
 * 若是通过 plugins { id 'com.android.application' } 形式，需要在 AfterEvaluate 阶段才能
 */
val Project.requireAppExtension: AppExtension
    get() = try {
        this.extensions.getByName("android") as AppExtension
    } catch (e: Exception) {
        e.printStackTrace()
        throw IllegalStateException("If you applied your android application plugin by id, then require this AfterEvaluate.")
    }

@AfterEvaluation
val Project.findAppPlugin: AppPlugin? get() = this.plugins.findPlugin(AppPlugin::class.java)

@AfterEvaluation
val Project.requireExtExtension: DefaultExtraPropertiesExtension
    get() = this.extensions.getByName("ext") as DefaultExtraPropertiesExtension

fun Project.findProjectProp(key: String): String? = this.gradle.startParameter.projectProperties[key]



enum class SourceLanguage { JAVA, KOTLIN, GROOVY }

val Project.mainSourceSet: SourceSet?
    get() = (this.properties["sourceSets"] as SourceSetContainer).findByName("main")

@AfterEvaluation
fun Project.getSourceSetDirs(language: SourceLanguage): List<File> {

    val mainSourceSet = this.mainSourceSet
    requireNotNull(mainSourceSet) { println("mainSourceSet is null, so sourceDir returned empty");return emptyList() }

    return when (language) {
        SourceLanguage.JAVA -> mainSourceSet.java.srcDirs
        SourceLanguage.KOTLIN -> ((mainSourceSet.extensions as Convention).plugins["kotlin"] as DefaultKotlinSourceSet).kotlin.srcDirs
        SourceLanguage.GROOVY -> ((mainSourceSet.extensions as Convention).plugins["groovy"] as DefaultGroovySourceSet).groovy.srcDirs
    }.filter { it.exists() }
}

package me.yangxiaobin.plugin

import me.yangxiaobin.lib.log.LogLevel
import me.yangxiaobin.lib.log.log
import me.yangxiaobin.plugin.log.BuildSrcLogger
import me.yangxiaobin.plugin.transform.HookArtifactTransform
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.transform.TransformParameters
import org.gradle.api.artifacts.transform.TransformSpec
import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.AttributesSchema

class BasePlugin : Plugin<Project> {

    private val logI = BuildSrcLogger.log(LogLevel.INFO,"")

    override fun apply(p: Project) {

        logI("applied buildSrc basePlugin")

        val artifactType = Attribute.of("artifactType", String::class.java)
        val hooked = Attribute.of("hooked", Boolean::class.javaObjectType)

        // 1. Add the attribute to the schema
        p.dependencies.attributesSchema { schema: AttributesSchema ->
            schema.attribute(hooked)
        }
        // 2. All JAR files are not minified
        p.dependencies
            .artifactTypes
            .getByName("jar")
            .attributes
            .attribute(hooked, false)


        // 3. Request hooked=true on all resolvable configurations
        p.configurations
//            .filter { it.name == "implementation" }
            .filterNot { it.name.startsWith("test") }
            .filterNot { it.name.startsWith("compileOnly") }
            .forEach { config ->
                p.afterEvaluate {
                    if (config.isCanBeResolved) {
                        config.attributes.attribute(hooked, true)
                    }
                }
            }


        // 4. Add the dependencies which will be transformed
        p.dependencies.registerTransform(HookArtifactTransform::class.java) { spec: TransformSpec<TransformParameters.None> ->

            spec.from
//                .attribute(artifactType, "jar")
//                .attribute(artifactType, "java-classes-directory")
                .attribute(hooked, false)

            spec.to
//                .attribute(artifactType, "jar")
                .attribute(hooked, true)

        }
    }
}

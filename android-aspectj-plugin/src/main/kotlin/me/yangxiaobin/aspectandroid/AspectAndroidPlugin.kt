package me.yangxiaobin.aspectandroid

import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.pipeline.TransformTask
import me.yangxiaobin.lib.BasePlugin
import me.yangxiaobin.lib.ext.getAppExtension
import me.yangxiaobin.lib.log.ILog
import me.yangxiaobin.lib.log.LogLevel
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import java.util.*

class AspectAndroidPlugin : BasePlugin() {

    override val TAG: String get() = "AAP"

    private val ext: AspectAndroidExt by lazy { mProject.extensions.getByType(AspectAndroidExt::class.java) }

    override val myLogger: ILog
        get() = super.myLogger.copy().setLevel(LogLevel.DEBUG)


    override fun apply(p: Project) {
        super.apply(p)

        logI("${p.name} applied AspectAndroidPlugin.")

        mProject.extensions.create("aspectAndroid", AspectAndroidExt::class.java)

        val aspectTransform = AspectTransform(mProject)

        p.afterEvaluate {
            logI("Resolved aspectJrt version :${ext.aspectJrtVersion}")
            p.getAppExtension?.registerTransform(aspectTransform)
            p.dependencies.add("implementation", "org.aspectj:aspectjrt:${ext.aspectJrtVersion}")
            createAjcCompileTask(aspectTransform)
        }

    }

    private fun createAjcCompileTask(aspectTransform: AspectTransform) {

        mProject.getAppExtension?.applicationVariants?.all { variant: ApplicationVariant ->

            // ':androidapp:transformClassesWithAspectTransformForAppstoreDebug'
            val aspectTransformTaskName =
                "transformClassesWith${aspectTransform.name.capitalize(Locale.getDefault())}For${
                    variant.name.capitalize(Locale.getDefault())
                }"
            val aspectTransformTask: TaskProvider<TransformTask> =
                mProject.tasks.named(aspectTransformTaskName, TransformTask::class.java)


            val ajcCompileTaskName =
                "ajcCompile${variant.name.capitalize(Locale.getDefault())}After${aspectTransform.name}"
            val ajcCompileTask: TaskProvider<AjcCompileTask> =
                mProject.tasks.register(ajcCompileTaskName, AjcCompileTask::class.java)

            aspectTransformTask.configure { transform: TransformTask ->
                transform.finalizedBy(ajcCompileTask)
                val ajc: AjcCompileTask = ajcCompileTask.get()
                ajc.inputDir = transform.outputs.files.singleFile
                ajc.outputDir = transform.outputs.files.singleFile
                ajc.variantName = variant.name
            }

        }
    }


}

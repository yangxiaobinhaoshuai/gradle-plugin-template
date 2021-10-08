package me.yangxiaobin.plugin.transform

import org.gradle.api.artifacts.transform.*
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.CompileClasspath

abstract class HookArtifactTransform : TransformAction<TransformParameters.None> {

    @get:CompileClasspath
    @get:InputArtifactDependencies
    abstract val transitiveDependencies: FileCollection

    @get:InputArtifact
    abstract val inputArtifact: Provider<FileSystemLocation>


    override fun transform(outputs: TransformOutputs) {
        val inputName = inputArtifact.get().asFile.name
        println("-------> input file name :$inputName , deps :${transitiveDependencies.asPath}")
        outputs.file(inputArtifact)
    }
}

package me.yangxiaobin.annotation_procssor

import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

class MyAnnotationProcessor : AbstractProcessor() {

    private lateinit var messager: Messager

    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)

    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {

        messager = processingEnv.messager

        println("---->process , annotations :$annotations , roundEnv :$roundEnv ")

        info("---->info , annotations :$annotations , roundEnv :$roundEnv ")

        return false
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.RELEASE_6
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf("me.yangxiaobin.annotation.CustomAnnotation")
    }

    override fun getSupportedOptions(): MutableSet<String> {
        return super.getSupportedOptions()
    }

    private fun info(content: String) {
        log(Diagnostic.Kind.NOTE, content)
    }

    private fun error(content: String) {
        log(Diagnostic.Kind.ERROR, content)
    }

    private fun log(kind: Diagnostic.Kind, content: String) {
        messager.printMessage(kind, "$content\r\n")
    }
}

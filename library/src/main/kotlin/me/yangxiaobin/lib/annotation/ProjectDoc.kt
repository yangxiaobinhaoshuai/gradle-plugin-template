package me.yangxiaobin.lib.annotation

/**
 * Means annotated values are ONLY available in project.afterEvaluate{ }.
 */
@Target(AnnotationTarget.PROPERTY,AnnotationTarget.FUNCTION)
annotation class AfterEvaluation

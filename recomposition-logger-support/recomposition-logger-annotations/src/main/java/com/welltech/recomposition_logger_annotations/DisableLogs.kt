package com.welltech.recomposition_logger_annotations

/**
 * disable logs and highlighting for composable function
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class DisableLogs
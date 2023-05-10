package com.welltech.compiler_plugin.generations

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.name.FqName

val composableAnnotationName get() = FqName("androidx.compose.runtime.Composable")
val disableLogAnnotationName get() = FqName("com.welltech.recomposition_logger_annotations.DisableLogs")
val logArgumentAnnotationName = FqName("com.welltech.recomposition_logger_annotations.LogArgument")

fun IrFunction.shouldAddLogsAndHighlight(pluginContext: IrPluginContext): Boolean {
    val isAnonymousFunction = this is IrSimpleFunction && (name.toString() == "<anonymous>")

    return body != null
        && hasAnnotation(composableAnnotationName)
        && !isAnonymousFunction
        && !hasAnnotation(disableLogAnnotationName)
        && returnType == pluginContext.irBuiltIns.unitType
}

package com.welltech.compiler_plugin.generations.logging

import com.welltech.compiler_plugin.generations.logArgumentAnnotationName
import org.jetbrains.kotlin.backend.common.extensions.FirIncompatiblePluginAPI
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.addArgument
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.name.FqName

class DefaultLogProvider(
    private val tag: String,
    pluginContext: IrPluginContext,
) : LogProvider {
    private val typeString = pluginContext.irBuiltIns.stringType

    @OptIn(FirIncompatiblePluginAPI::class)
    private val funLog = pluginContext.referenceFunctions(FqName("com.welltech.recomposition_logger_runtime.LogComposition"))
        .single {
            val parameters = it.owner.valueParameters
            parameters.size == 2
                && parameters[0].type == typeString
                && parameters[1].type == typeString
        }

    override fun IrBuilderWithScope.logCompositionCall(function: IrFunction): IrCall {
        return irCall(funLog).also { call ->
            val concat = irConcat()
            concat.addArgument(irString("${function.name}"))

            val logParameters = function.valueParameters
                .filter { it.hasAnnotation(logArgumentAnnotationName) }
            logParameters.forEachIndexed { index, valueParameter ->
                if (index == 0) {
                    concat.addArgument(irString("("))
                }
                concat.addArgument(irString("${valueParameter.name}="))
                concat.addArgument(irGet(valueParameter))
                if (index == logParameters.lastIndex) {
                    concat.addArgument(irString(")"))
                } else {
                    concat.addArgument(irString(", "))
                }
            }
            call.putValueArgument(0, concat)
            call.putValueArgument(1, irString(tag))
        }
    }
}

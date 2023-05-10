package com.welltech.compiler_plugin.generations.logging

import com.welltech.compiler_plugin.generations.logArgumentAnnotationName
import org.jetbrains.kotlin.backend.common.extensions.FirIncompatiblePluginAPI
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.addArgument
import org.jetbrains.kotlin.ir.types.createType
import org.jetbrains.kotlin.ir.types.impl.IrTypeBase
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class RebuggerLogProvider(
    private val pluginContext: IrPluginContext,
) : LogProvider {

    @OptIn(FirIncompatiblePluginAPI::class)
    private val funLog = pluginContext.referenceFunctions(FqName("com.theapache64.rebugger.Rebugger"))
        .single {
            val parameters = it.owner.valueParameters
            parameters.size == 2
        }

    @OptIn(FirIncompatiblePluginAPI::class)
    private val createMapFun = pluginContext.referenceFunctions(FqName("kotlin.collections.mapOf"))
        .single {
            val parameters = it.owner.valueParameters
            parameters.size == 1
                && parameters[0].varargElementType != null
        }

    override fun IrBuilderWithScope.logCompositionCall(function: IrFunction): IrCall {
        return irCall(funLog).also { call ->
            call.putValueArgument(0, createArgumentsMap(function))
            val functionName = irConcat()
            functionName.addArgument(irString(function.name.toString()))
            function.valueParameters
                .filter { it.hasAnnotation(logArgumentAnnotationName) }
                .forEach { valueParameter ->
                    functionName.addArgument(irString(" ${valueParameter.name}="))
                    functionName.addArgument(irGet(valueParameter))
                }
            call.putValueArgument(1, functionName)
        }
    }

    private fun IrBuilderWithScope.createArgumentsMap(function: IrFunction): IrExpression {
        return irCall(createMapFun).also { call ->
            val pairClass = pluginContext.referenceClass(ClassId(FqName("kotlin"), Name.identifier("Pair")))!!

            val values = function.valueParameters.map { inputParameter ->
                irCall(pairClass.constructors.first()).also { pairConstructor ->
                    pairConstructor.putValueArgument(0, irString(inputParameter.name.toString()))
                    pairConstructor.putValueArgument(1, irGet(inputParameter))
                }
            }

            val varargType = pairClass.createType(
                hasQuestionMark = false,
                arguments = listOf(pluginContext.irBuiltIns.stringType, pluginContext.irBuiltIns.anyType).map {
                    it as IrTypeBase
                },
            )

            call.putValueArgument(0, irVararg(varargType, values))
        }
    }

}

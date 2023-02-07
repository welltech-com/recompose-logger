package com.welltech.compiler_plugin.generations

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.FirIncompatiblePluginAPI
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.addArgument
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.name.FqName

class LogsGenerationExtension(private val tag: String) : IrGenerationExtension {
  override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {

    val transformer = ComposeLogTransformer(
      pluginContext = pluginContext,
      tag = tag
    )
    moduleFragment.transform(transformer, null)
  }
}

private class ComposeLogTransformer(
  private val pluginContext: IrPluginContext,
  private val tag: String
) : IrElementTransformerVoidWithContext() {

  private val typeString = pluginContext.irBuiltIns.stringType

  @OptIn(FirIncompatiblePluginAPI::class)
  private val funLog = pluginContext.referenceFunctions(FqName("com.welltech.recomposition_logger_runtime.LogComposition"))
    .single {
      val parameters = it.owner.valueParameters
      parameters.size == 2
              && parameters[0].type == typeString
              && parameters[1].type == typeString
    }

  override fun visitFunctionNew(declaration: IrFunction): IrStatement {
    if (declaration.shouldAddLogsAndHighlight(pluginContext)) {
      declaration.body = withLogComposition(declaration)
    }
    return super.visitFunctionNew(declaration)
  }

  private fun withLogComposition(
    function: IrFunction,
  ): IrBlockBody {
    return DeclarationIrBuilder(pluginContext, function.symbol).irBlockBody {
      +logCompositionCall(function)
      function.body?.statements?.forEach { statement ->
        +statement
      }
    }
  }

  private fun IrBuilderWithScope.logCompositionCall(
    function: IrFunction
  ): IrCall {
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

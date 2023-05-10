package com.welltech.compiler_plugin.generations

import com.welltech.compiler_plugin.generations.logging.DefaultLogProvider
import com.welltech.compiler_plugin.generations.logging.LogProvider
import com.welltech.compiler_plugin.generations.logging.RebuggerLogProvider
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.util.statements

class LogsGenerationExtension(
    private val tag: String,
    private val useRebugger: Boolean,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val logProvider = if (useRebugger) {
            RebuggerLogProvider(pluginContext)
        } else {
            DefaultLogProvider(tag, pluginContext)
        }

        val transformer = ComposeLogTransformer(
            pluginContext = pluginContext,
            logProvider = logProvider,
        )
        moduleFragment.transform(transformer, null)
    }
}

private class ComposeLogTransformer(
    private val pluginContext: IrPluginContext,
    private val logProvider: LogProvider,
) : IrElementTransformerVoidWithContext() {
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
            with(logProvider) {
                +logCompositionCall(function)
            }
            function.body?.statements?.forEach { statement ->
                +statement
            }
        }
    }
}

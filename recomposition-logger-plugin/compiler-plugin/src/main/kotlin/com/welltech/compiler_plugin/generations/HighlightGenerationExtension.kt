package com.welltech.compiler_plugin.generations

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrStatementContainer
import org.jetbrains.kotlin.ir.types.isClassType
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.FqNameUnsafe
import com.welltech.compiler_plugin.debug_logger.Logger
import org.jetbrains.kotlin.backend.common.extensions.FirIncompatiblePluginAPI

class HighlightGenerationExtension(
    private val logger: Logger
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        moduleFragment.transform(HighlightTransformer(pluginContext, logger), null)
    }
}

@OptIn(FirIncompatiblePluginAPI::class)
private class HighlightTransformer(
    private val pluginContext: IrPluginContext,
    private val logger: Logger
) : IrElementTransformerVoidWithContext() {

    private val funHighlight = pluginContext.referenceFunctions(FqName("com.welltech.recomposition_logger_runtime.highlight.highlightRecomposition"))
            .single {
                val parameters = it.owner.valueParameters
                parameters.size == 1
                        && parameters[0].type == pluginContext.irBuiltIns.stringType
            }

    private val initialModifier = pluginContext.referenceClass(FqName("androidx.compose.ui.Modifier.Companion"))!!

    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        if (declaration.shouldAddLogsAndHighlight(pluginContext) ) {
            logger.logMsg("visit new composable fun: ${declaration.name}")
            DeclarationIrBuilder(pluginContext, declaration.symbol)
                .findAndModifyComposeFunctions(declaration.name.toString(), declaration.body!!.statements)
        }

        return super.visitFunctionNew(declaration)
    }

    private fun IrBuilderWithScope.findAndModifyComposeFunctions(funName: String, statements: List<IrStatement>) {
        for (statement in statements) {
            logger.logMsg("---statement: ${statement.dump()}")
            if (statement.isComposableCallWithModifier()) {
                logger.logMsg("-----is Composable call, add highlight modifier")
                addHighlightModifier(funName, statement as IrCall)
            } else if (statement is IrStatementContainer) {
                findAndModifyComposeFunctions(funName, statement.statements)
            }
        }
    }

    private fun IrStatement.isComposableCallWithModifier(): Boolean {
        return this is IrCall
                && symbol.owner.hasAnnotation(composableAnnotationName)
                && getModifierArgument() != null
    }

    private fun IrBuilderWithScope.addHighlightModifier(funName: String, irCallStatement: IrCall) {
        val modifierArgument = irCallStatement.getModifierArgument()
            ?: throw IllegalArgumentException("missing modifier argument in function ${irCallStatement.symbol.owner.name} ")

        val currentModifierValue = irCallStatement.getValueArgument(modifierArgument.index)
        logger.logMsg("-----current modifier: ${currentModifierValue?.dump()}")
        val newModifierValue = irCall(funHighlight).apply {
            extensionReceiver = currentModifierValue ?: irGetObject(initialModifier)
            putValueArgument(0, irString(funName))
        }
        logger.logMsg("-----new modifier: ${newModifierValue.dump()}")

        irCallStatement.putValueArgument(modifierArgument.index, newModifierValue)
    }

    private fun IrCall.getModifierArgument(): IrValueParameter? {
        return symbol.owner.valueParameters.firstOrNull {
            it.type.isClassType(FqNameUnsafe("androidx.compose.ui.Modifier"), false)
        }
    }
}
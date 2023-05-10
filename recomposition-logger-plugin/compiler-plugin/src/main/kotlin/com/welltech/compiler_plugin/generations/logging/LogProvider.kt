package com.welltech.compiler_plugin.generations.logging

import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrCall

interface LogProvider {
    fun IrBuilderWithScope.logCompositionCall(
        function: IrFunction,
    ): IrCall
}

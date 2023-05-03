package com.welltech.compiler_plugin

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import com.welltech.compiler_plugin.generations.HighlightGenerationExtension
import com.welltech.compiler_plugin.generations.LogsGenerationExtension
import com.welltech.compiler_plugin.debug_logger.EmptyLogger
import com.welltech.compiler_plugin.debug_logger.FileLogger
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
@AutoService(ComponentRegistrar::class)
class PluginComponentRegistrar : ComponentRegistrar {

    override fun registerProjectComponents(
        project: MockProject,
        configuration: CompilerConfiguration,
    ) {
        val enabled = configuration.get(
            PluginCommandLineProcessor.Keys.logEnabled,
            BuildConfig.DEFAULT_RECOMPOSITION_LOGS_ENABLED,
        )
        val logTag = configuration.get(
            PluginCommandLineProcessor.Keys.logTag,
            BuildConfig.DEFAULT_RECOMPOSITION_LOGS_TAG,
        )

        val logFile = configuration.get(
            PluginCommandLineProcessor.Keys.logFile,
        )

        val useRebugger = configuration.get(
            PluginCommandLineProcessor.Keys.useRebugger,
            BuildConfig.DEFAULT_USE_REBUGGER,
        )

        val logger = when (logFile) {
            null -> EmptyLogger()
            else -> FileLogger(logFile)
        }

        if (enabled) {
            IrGenerationExtension.registerExtension(
                project = project,
                extension = LogsGenerationExtension(tag = logTag, useRebugger = useRebugger),
            )
            IrGenerationExtension.registerExtension(
                project = project,
                extension = HighlightGenerationExtension(logger),
            )
        }
    }
}



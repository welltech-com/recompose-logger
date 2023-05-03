package com.welltech.compiler_plugin

import com.google.auto.service.AutoService
import com.welltech.compiler_plugin.BuildConfig.KEY_RECOMPOSITION_LOGS_ENABLED
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import com.welltech.compiler_plugin.BuildConfig.KEY_RECOMPOSITION_LOGS_TAG
import com.welltech.compiler_plugin.BuildConfig.KEY_LOG_FILE
import com.welltech.compiler_plugin.BuildConfig.KOTLIN_PLUGIN_ID
import com.welltech.compiler_plugin.BuildConfig.KEY_USE_REBUGGER
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import java.io.File

@OptIn(ExperimentalCompilerApi::class)
@AutoService(CommandLineProcessor::class)
class PluginCommandLineProcessor : CommandLineProcessor {
    object Keys {
        val logFile = KEY_LOG_FILE.toConfigKey<File>()
        val logEnabled = KEY_RECOMPOSITION_LOGS_ENABLED.toConfigKey<Boolean>()
        val logTag = KEY_RECOMPOSITION_LOGS_TAG.toConfigKey<String>()
        val useRebugger = KEY_USE_REBUGGER.toConfigKey<Boolean>()
        private inline fun <T> String.toConfigKey() = CompilerConfigurationKey<T>(this)
    }

    override val pluginId: String = KOTLIN_PLUGIN_ID

    override val pluginOptions: Collection<CliOption> = listOf(
        CliOption(
            optionName = KEY_LOG_FILE,
            valueDescription = "absolute file path",
            description = "file for printing compiler debug logs",
            required = false,
        ),
        CliOption(
            optionName = KEY_RECOMPOSITION_LOGS_ENABLED,
            valueDescription = "bool <true | false>",
            description = "Add logging for @Composable functions",
            required = false,
        ),
        CliOption(
            optionName = KEY_RECOMPOSITION_LOGS_TAG,
            valueDescription = "String",
            description = "tag for android.util.Log",
            required = false,
        ),
        CliOption(
            optionName = KEY_USE_REBUGGER,
            valueDescription = "bool <true | false>",
            description = "use rebugger for logging recompositions. More details: https://github.com/theapache64/rebugger",
            required = false,
        ),
    )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration,
    ) {
        return when (option.optionName) {
            KEY_RECOMPOSITION_LOGS_ENABLED -> configuration.put(Keys.logEnabled, value.toBoolean())
            KEY_RECOMPOSITION_LOGS_TAG -> configuration.put(Keys.logTag, value)
            KEY_LOG_FILE -> configuration.put(Keys.logFile, File(value))
            KEY_USE_REBUGGER -> configuration.put(Keys.useRebugger, value.toBoolean())
            else -> throw IllegalArgumentException("Unexpected config option ${option.optionName}")
        }
    }
}

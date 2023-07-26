package com.welltech.gradle_plugin

import com.welltech.gradle_plugin.extension.RecompositionLoggerGradleExtension
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.*

class RecompositionLoggerGradlePlugin : KotlinCompilerPluginSupportPlugin {
    override fun apply(target: Project): Unit = with(target) {
        extensions.create("recompositionLogger", RecompositionLoggerGradleExtension::class.java)
        afterEvaluate {
            val extension = project.extensions.getByType(RecompositionLoggerGradleExtension::class.java)
            val supportLibDependency = extension.supportLibConfigurationName ?: "debugImplementation"
            val useRebugger = extension.useRebugger ?: BuildConfig.DEFAULT_USE_REBUGGER

            val runtimeLibs = if (useRebugger) {
                listOf(BuildConfig.RUNTIME_LIB, BuildConfig.REBUGGER_LIB)
            } else {
                listOf(BuildConfig.RUNTIME_LIB)
            }

            runtimeLibs.forEach { dependencies.add(supportLibDependency, it) }

            dependencies.add("implementation", BuildConfig.ANNOTATIONS_LIB)
        }
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
        return true
    }

    override fun getCompilerPluginId(): String = BuildConfig.KOTLIN_PLUGIN_ID

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = BuildConfig.KOTLIN_PLUGIN_GROUP,
        artifactId = BuildConfig.KOTLIN_PLUGIN_NAME,
        version = BuildConfig.KOTLIN_PLUGIN_VERSION,
    )

    override fun applyToCompilation(
        kotlinCompilation: KotlinCompilation<*>,
    ): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        val extension = project.extensions.getByType(RecompositionLoggerGradleExtension::class.java)

        val logFile = extension.logFile

        val pluginEnabled = extension.enabled ?: kotlin.run {
            project.gradle.startParameter.taskRequests.any {
                it.args.any { it.endsWith("Debug") }
            }
        }
        if (pluginEnabled) {
            val kotlinVersion = project.getKotlinPluginVersion()
            if (kotlinVersion != BuildConfig.KOTLIN_VERSION) {
                error("Require kotlin version ${BuildConfig.KOTLIN_VERSION} but current: $kotlinVersion")
            }
        }
        val logsTag = extension.tag ?: BuildConfig.DEFAULT_RECOMPOSITION_LOGS_TAG
        val useRebugger = extension.useRebugger ?: BuildConfig.DEFAULT_USE_REBUGGER

        if (useRebugger) {
            project.repositories.maven {
                it.setUrl("https://jitpack.io")
            }
        }

        return project.provider {
            listOfNotNull(
                SubpluginOption(
                    key = BuildConfig.KEY_RECOMPOSITION_LOGS_ENABLED,
                    value = pluginEnabled.toString(),
                ),
                SubpluginOption(
                    key = BuildConfig.KEY_RECOMPOSITION_LOGS_TAG,
                    value = logsTag,
                ),
                logFile?.let {
                    SubpluginOption(
                        key = BuildConfig.KEY_LOG_FILE,
                        value = it.absolutePath,
                    )
                },
                SubpluginOption(
                    key = BuildConfig.KEY_USE_REBUGGER,
                    value = useRebugger.toString(),
                ),
            )
        }
    }
}

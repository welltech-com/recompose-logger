package com.welltech.gradle_plugin

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import com.welltech.gradle_plugin.extension.RecompositionLoggerGradleExtension

class RecompositionLoggerGradlePlugin : KotlinCompilerPluginSupportPlugin {
    override fun apply(target: Project): Unit = with(target) {
        extensions.create("recompositionLogger", RecompositionLoggerGradleExtension::class.java)
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
        return true
    }

    override fun getCompilerPluginId(): String = BuildConfig.KOTLIN_PLUGIN_ID

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = BuildConfig.KOTLIN_PLUGIN_GROUP,
        artifactId = BuildConfig.KOTLIN_PLUGIN_NAME,
        version = BuildConfig.KOTLIN_PLUGIN_VERSION
    )

    override fun applyToCompilation(
        kotlinCompilation: KotlinCompilation<*>
    ): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        val extension = project.extensions.getByType(RecompositionLoggerGradleExtension::class.java)

        val logFile = extension.logFile

        val pluginEnabled = extension.enabled ?: kotlin.run {
            project.gradle.startParameter.taskRequests.any {
                it.args.any { it.endsWith("Debug") }
            }
        }
        val logsTag = extension.tag ?: BuildConfig.DEFAULT_RECOMPOSITION_LOGS_TAG

        val supportLibDependency = extension.supportLibDependency

        kotlinCompilation.dependencies {
            when(supportLibDependency) {
                "none" -> { /*do nothing*/ }
                "api" -> {
                    if (pluginEnabled) api(BuildConfig.RUNTIME_LIB)
                    api(BuildConfig.ANNOTATIONS_LIB)
                }
                "compileOnly" -> {
                    if (pluginEnabled) compileOnly(BuildConfig.RUNTIME_LIB)
                    compileOnly(BuildConfig.ANNOTATIONS_LIB)
                }
                else -> {
                    if (pluginEnabled) implementation(BuildConfig.RUNTIME_LIB)
                    implementation(BuildConfig.ANNOTATIONS_LIB)
                }
            }
        }

        return project.provider {
            listOfNotNull(
                SubpluginOption(
                    key = BuildConfig.KEY_RECOMPOSITION_LOGS_ENABLED,
                    value = pluginEnabled.toString()
                ),
                SubpluginOption(
                    key = BuildConfig.KEY_RECOMPOSITION_LOGS_TAG,
                    value = logsTag
                ),
                logFile?.let {
                    SubpluginOption(
                        key = BuildConfig.KEY_LOG_FILE,
                        value = it.absolutePath
                    )
                }
            )
        }
    }
}

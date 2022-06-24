package com.welltech.gradle_plugin

import org.gradle.api.Project
import com.welltech.gradle_plugin.extension.RecompositionLoggerGradleExtension

fun Project.recompositionLogger(configuration: RecompositionLoggerGradleExtension.() -> Unit) {
    extensions.configure("recompositionLogger", configuration)
}
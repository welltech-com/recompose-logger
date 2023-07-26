package com.welltech.gradle_plugin.extension

import java.io.File

abstract class RecompositionLoggerGradleExtension {

    abstract var logFile: File?

    /**
     * available: none, implementation, api, compileOnly. Default - implementation
     */
    @Deprecated("use supportLibConfigurationName instead", level = DeprecationLevel.ERROR)
    abstract var supportLibDependency: String?

    /**
     * configuration name for dependencies. By default: debugImplementation
     */
    abstract var supportLibConfigurationName: String?

    abstract var enabled: Boolean?

    abstract var tag: String?

    abstract var useRebugger: Boolean?

}


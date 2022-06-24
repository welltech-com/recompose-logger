package com.welltech.gradle_plugin.extension

import java.io.File

abstract class RecompositionLoggerGradleExtension {

  abstract var logFile: File?

  /**
   * available: none, implementation, api, compileOnly. Default - implementation
   */
  abstract var supportLibDependency: String?

  abstract var enabled: Boolean?

  abstract var tag: String?

}


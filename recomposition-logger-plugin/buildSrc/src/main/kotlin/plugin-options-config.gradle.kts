plugins {
    id("com.github.gmazzo.buildconfig")
}

buildConfig {
    buildConfigField("String", "KEY_RECOMPOSITION_LOGS_ENABLED", "\"recomposition_log_enabled\"")
    buildConfigField("boolean", "DEFAULT_RECOMPOSITION_LOGS_ENABLED", "false")

    buildConfigField("String", "KEY_RECOMPOSITION_LOGS_TAG", "\"recomposition_log_tag\"")
    buildConfigField("String", "DEFAULT_RECOMPOSITION_LOGS_TAG", "\"RecompositionLog\"")

    buildConfigField("String", "KEY_USE_REBUGGER", "\"use_rebugger\"")
    buildConfigField("boolean", "DEFAULT_USE_REBUGGER", "false")

    buildConfigField("String", "KEY_LOG_FILE", "\"log_file\"")
}
import org.gradle.api.Project

val Project.pluginConfig: PluginConfig
    get() = PluginConfig(this)

class PluginConfig internal constructor(private val project: Project) {
    private val commonProperties = project.loadProperties("../common.properties")

    val kotlinVersion = commonProperties.getProperty("kotlin_version")

    val group = commonProperties.getProperty("group_id")
    val version = commonProperties.getProperty("version")

    val gradlePluginName = commonProperties.getProperty("plugin_artifact")
    val gradlePluginId = "$group.$gradlePluginName"

    val compilerPluginName = "recomposition-logger-compiler-plugin"
    val compilerPluginId = "$group.$compilerPluginName"

    private val runtimeLibArtifact = commonProperties.getProperty("runtime_artifact")
    private val annotationsLibArtifact = commonProperties.getProperty("annotations_artifact")

    val runtimeLib = "$group:$runtimeLibArtifact:$version"
    val annotationsLib = "$group:$annotationsLibArtifact:$version"

    val rebuggerLib = "com.github.theapache64:rebugger:1.0.0-alpha02"
}
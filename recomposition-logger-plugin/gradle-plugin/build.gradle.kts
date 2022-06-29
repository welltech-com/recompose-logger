import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("java-gradle-plugin")
  id("com.gradle.plugin-publish") version "1.0.0-rc-2"
  kotlin("jvm")
  id("com.github.gmazzo.buildconfig")
  id("plugin-options-config")
}

version = pluginConfig.version
group = pluginConfig.group

dependencies {
  implementation(kotlin("stdlib"))
  implementation(kotlin("gradle-plugin-api"))
}

buildConfig {
  packageName(pluginConfig.group + ".gradle_plugin")
  buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${pluginConfig.compilerPluginId}\"")
  buildConfigField("String", "KOTLIN_PLUGIN_GROUP", "\"${pluginConfig.group}\"")
  buildConfigField("String", "KOTLIN_PLUGIN_NAME", "\"${pluginConfig.compilerPluginName}\"")
  buildConfigField("String", "KOTLIN_PLUGIN_VERSION", "\"${pluginConfig.version}\"")
  buildConfigField("String", "RUNTIME_LIB", "\"${pluginConfig.runtimeLib}\"")
  buildConfigField("String", "ANNOTATIONS_LIB", "\"${pluginConfig.annotationsLib}\"")
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "11"
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

gradlePlugin {
  plugins {
    create(pluginConfig.gradlePluginName) {
      id = pluginConfig.gradlePluginId
      displayName = "Recomposition logger gradle plugin"
      implementationClass = "com.welltech.gradle_plugin.RecompositionLoggerGradlePlugin"
    }
  }
}

pluginBundle {
  // TODO set own github links
//  website = 'https://github.com/johndoe/GradlePlugins'
//  vcsUrl = 'https://github.com/johndoe/GradlePlugins'

  description = "This plugin makes it easy to debug composable functions by adding logs to each function.\n" +
          "The plugin also highlights composable functions during recomposition"

  version = pluginConfig.version
  tags = listOf("android", "compose", "jetpack compose")
}

publishing {
  repositories {
  // maybe remove when publish to gradlePluginPortal
    mavenLocal()
  }
  publications {
    maybeCreate<MavenPublication>("pluginMaven").apply {
      artifactId = pluginConfig.gradlePluginName
    }
  }
}

tasks.register("buildAndPublishToMavenLocal") {
// TODO change `publishToMavenLocal` to `publishPlugins` after register on gradle plugin portal
  dependsOn(tasks.named("assemble"), tasks.named("publishToMavenLocal"))
}
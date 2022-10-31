import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java-gradle-plugin")
    kotlin("jvm")
    id("com.github.gmazzo.buildconfig")
    id("plugin-options-config")
    `maven-publish`
    `signing`
    id("org.jetbrains.dokka") version "1.7.10"
}

version = pluginConfig.version
group = pluginConfig.group

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("gradle-plugin-api"))
    implementation(kotlin("gradle-plugin"))
}

buildConfig {
    packageName(pluginConfig.group + ".gradle_plugin")
    buildConfigField("String", "KOTLIN_VERSION", "\"${pluginConfig.kotlinVersion}\"")
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

val dokkaHtml by tasks.getting(org.jetbrains.dokka.gradle.DokkaTask::class)

val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    dependsOn(dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaHtml.outputDirectory)
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

extensions.configure<SigningExtension> {
    configureGpg()
    sign(publishing.publications)
}

publishing {
    setupPublishingRepositories(project)

    publications {
        create<MavenPublication>("default") {
            artifactId = pluginConfig.gradlePluginName
            from(components["java"])
            artifact(tasks.kotlinSourcesJar)
            artifact(javadocJar)

            pom {
                setupDefaultPom()
                name.set("Recomposition Logger gradle plugin")
                description.set(
                    "This plugin makes it easy to debug composable functions by adding logs to each function.\n" +
                        "The plugin also highlights composable functions during recomposition"
                )
                url.set("https://github.com/welltech-com/recompose-logger")
            }
        }
    }
}
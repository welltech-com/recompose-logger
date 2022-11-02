import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("com.github.gmazzo.buildconfig")
    `maven-publish`
    `signing`
    id("plugin-options-config")
    id("org.jetbrains.dokka") version "1.7.10"
}

group = pluginConfig.group
version = pluginConfig.version

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")

    compileOnly("com.google.auto.service:auto-service-annotations:1.0.1")
    kapt("com.google.auto.service:auto-service:1.0-rc7")
}

buildConfig {
    packageName(pluginConfig.group + ".compiler_plugin")
    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${pluginConfig.compilerPluginId}\"")
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

extensions.configure<SigningExtension> {
    configureGpg()
    sign(publishing.publications)
}

publishing {
    setupPublishingRepositories(project)

    publications {
        create<MavenPublication>("default") {
            artifactId = pluginConfig.compilerPluginName
            from(components["java"])
            artifact(tasks.kotlinSourcesJar)
            artifact(javadocJar)

            pom {
                setupDefaultPom()
                name.set("Recomposition Logger compiler plugin")
                description.set("Kotlin compiler plugin, that inject logs into composable functions")
                url.set("https://github.com/welltech-com/recompose-logger")
            }
        }
    }
}
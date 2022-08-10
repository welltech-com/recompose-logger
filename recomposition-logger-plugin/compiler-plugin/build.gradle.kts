import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("com.github.gmazzo.buildconfig")
    `maven-publish`
    `signing`
    id("plugin-options-config")
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

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

extensions.configure<SigningExtension> {
    sign(publishing.publications)
}

publishing {
    setupPublishingRepositories(project)

    publications {
        create<MavenPublication>("default") {
            artifactId = pluginConfig.compilerPluginName
            from(components["java"])
            artifact(tasks.kotlinSourcesJar)

            pom {
                setupDefaultPom()
                name.set("Recomposition Logger compiler plugin")
                description.set("Kotlin compiler plugin, that inject logs into composable functions")
                url.set("https://github.com/welltech-com/recompose-logger")
            }
        }
    }
}

tasks.register("buildAndPublishToMavenLocal") {
    dependsOn(tasks.named("assemble"), tasks.named("publishDefaultPublicationToMavenLocal"))
}

tasks.register("buildAndPublishToMavenRepository") {
    dependsOn(tasks.named("assemble"), tasks.named("publishDefaultPublicationToMavenRepository"))
}

tasks.register("buildAndPublishToSnapshotRepository") {
    dependsOn(tasks.named("assemble"), tasks.named("publishDefaultPublicationToMaven2Repository"))
}

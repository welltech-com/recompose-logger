buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${pluginConfig.kotlinVersion}")
    }
}

subprojects {
    repositories {
        mavenCentral()
    }

    tasks.register("buildAndPublishToMavenLocal") {
        dependsOn(tasks.named("assemble"), tasks.named("publishDefaultPublicationToMavenLocal"))
    }

    tasks.register("buildAndPublishToMavenRepository") {
        dependsOn(tasks.named("assemble"), tasks.named("publishDefaultPublicationToStagingRepository"))
    }

    tasks.register("buildAndPublishToSnapshotRepository") {
        dependsOn(tasks.named("assemble"), tasks.named("publishDefaultPublicationToSnapshotRepository"))
    }

}

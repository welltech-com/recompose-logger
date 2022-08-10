plugins {
    `maven-publish`
    `signing`
}

extensions.configure<SigningExtension> {
    sign(publishing.publications)
}

publishing {
    setupPublishingRepositories(project)

    task("sourceJar", Jar::class) {
        from("$projectDir/src/main/java")
        archiveClassifier.set("sources")
    }

    publications {
        create("AndroidLibrary", MavenPublication::class).apply {
            groupId = config.group
            version = config.version

            artifact("$buildDir/outputs/aar/${project.name}-release.aar")
            artifact(tasks.getByName("sourceJar"))

            pom {
                setupDefaultPom()

                withXml {
                    asNode().apply {
                        // dependencies
                        appendNode("dependencies").apply {
                            configurations.getByName("releaseCompileClasspath")
                                .resolvedConfiguration
                                .firstLevelModuleDependencies
                                .forEach {
                                    if (it.moduleVersion != "unspecified") {
                                        val dependency = appendNode("dependency")
                                        dependency.appendNode("groupId", it.moduleGroup)
                                        dependency.appendNode("artifactId", it.moduleName)
                                        dependency.appendNode("version", it.moduleVersion)
                                    }
                                }
                        }
                    }
                }
            }
        }
    }

    tasks.register("buildAndPublishToMavenLocal") {
        dependsOn(tasks.named("assemble"), tasks.named("publishAndroidLibraryPublicationToMavenLocal"))
    }

    tasks.register("buildAndPublishToMavenRepository") {
        dependsOn(tasks.named("assemble"), tasks.named("publishAndroidLibraryPublicationToMavenRepository"))
    }

    tasks.register("buildAndPublishToSnapshotRepository") {
        dependsOn(tasks.named("assemble"), tasks.named("publishAndroidLibraryPublicationToMaven2Repository"))
    }
}
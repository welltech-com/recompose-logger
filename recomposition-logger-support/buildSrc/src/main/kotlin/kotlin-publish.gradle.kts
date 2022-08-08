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

    tasks["publishAndroidLibraryPublicationToMavenLocal"].apply {
        dependsOn(project.tasks["assemble"])
    }
    tasks["publishAndroidLibraryPublicationToMavenRepository"].apply {
        dependsOn(project.tasks["assemble"])
    }
    tasks["publishAndroidLibraryPublicationToMaven2Repository"].apply {
        dependsOn(project.tasks["assemble"])
    }
}
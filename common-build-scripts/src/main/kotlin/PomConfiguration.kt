import org.gradle.api.publish.maven.MavenPom

fun MavenPom.setupDefaultPom() {
    licenses {
        license {
            name.set("The Apache License, Version 2.0")
            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
        }
    }

    developers {
        listOf(
            "Evgeniy Kurinnoy",
            "Aleksandr Trykashnyi"
        ).forEach { devName ->
            developer {
                name.set(devName)
                organization.set("Welltech")
                organizationUrl.set("https://welltech.com/")
            }
        }
    }

    scm {
        connection.set("scm:git:git://github.com/welltech-com/recompose-logger.git")
        developerConnection.set("scm:git:ssh://github.com:welltech-com/recompose-logger.git")
        url.set("https://github.com/welltech-com/recompose-logger")
    }
}
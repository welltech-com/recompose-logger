import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension

fun PublishingExtension.setupPublishingRepositories(project: Project) {
    with(project) {
        val localProperties = loadProperties("../local.properties")
        val ossUsername = localProperties.getProperty("oss_username")
        val ossPassword = localProperties.getProperty("oss_password")
       this@setupPublishingRepositories.repositories {
            mavenLocal()
            maven {
                mavenContent {
                    releasesOnly()
                }
                url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = ossUsername
                    password = ossPassword
                }
            }
            maven {
                mavenContent {
                    snapshotsOnly()
                }
                url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                credentials {
                    username = ossUsername
                    password = ossPassword
                }
            }
        }
    }
}
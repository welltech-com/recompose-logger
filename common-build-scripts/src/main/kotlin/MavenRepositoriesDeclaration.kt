import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension

fun PublishingExtension.setupPublishingRepositories(project: Project) {
    with(project) {
        val ossUsername = System.getenv()["OSS_USERNAME"]
        val ossPassword = System.getenv()["OSS_PASSWORD"]
        this@setupPublishingRepositories.repositories {
            mavenLocal()
            maven {
                name = "Staging"
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
                name = "Snapshot"
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
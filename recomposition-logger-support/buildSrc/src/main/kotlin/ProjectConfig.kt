import org.gradle.api.Project
import java.io.FileInputStream
import java.util.*

val Project.config: ProjectConfig
    get() = ProjectConfig(this)

class ProjectConfig internal constructor(private val project: Project) {
    private val commonProperties = project.loadProperties("../common.properties")

    val kotlinVersion = commonProperties.getProperty("kotlin_version")

    val group = commonProperties.getProperty("group_id")
    val version = commonProperties.getProperty("version")

    val runtimeLibArtifact = commonProperties.getProperty("runtime_artifact")
    val annotationsLibArtifact = commonProperties.getProperty("annotations_artifact")
}
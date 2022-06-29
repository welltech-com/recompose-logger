import org.gradle.api.Project
import java.io.FileInputStream
import java.util.*

private val propertiesCache = mutableMapOf<String, PropertiesCache>()

private data class PropertiesCache(val properties: Properties, val lastModified: Long)

fun Project.loadProperties(path: String): Properties {
    val cacheKey = "${rootProject.name}/$path"
    val cache = propertiesCache[cacheKey]

    val propsFile = rootProject.file(path)
    val lastModified = propsFile.lastModified()
    if (cache != null && cache.lastModified == lastModified) {
        return cache.properties
    }

    val newProperties = Properties().apply {
        load(FileInputStream(propsFile))
    }

    propertiesCache[cacheKey] = PropertiesCache(newProperties, lastModified)
    return newProperties
}
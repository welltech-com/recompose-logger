import org.gradle.plugins.signing.SigningExtension

fun SigningExtension.configureGpg() {
    isRequired = false
    useInMemoryPgpKeys(System.getenv("SIGNING_KEY"), System.getenv("SIGNING_PASS"))
}

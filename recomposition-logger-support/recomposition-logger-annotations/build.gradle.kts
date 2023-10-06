plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-publish")
}

android {
    namespace = "com.welltech.recomposition_logger_annotations"
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 33

        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
        consumerProguardFile("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    file("proguard-rules.pro")
                )
            )
        }
    }

    buildFeatures {
        buildConfig = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

publishing {
    publications {
        getByName<MavenPublication>("AndroidLibrary").apply {
            artifactId = config.annotationsLibArtifact
            pom {
                name.set("Recomposition Logger annotations")
                description.set("Support library for Recomposition Logger plugin that contains annotations")
                url.set("https://github.com/welltech-com/recompose-logger")
            }
        }
    }
}

dependencies {
    compileOnly("androidx.compose.ui:ui-tooling:${Versions.composeToolingVersion}")
}

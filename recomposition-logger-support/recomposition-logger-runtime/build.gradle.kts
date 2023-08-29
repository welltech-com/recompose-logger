plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-publish")
}

android {
    namespace = "com.welltech.recomposition_logger_support"
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 33

        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
        consumerProguardFile("consumer-rules.pro")
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
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
        compose = true
        buildConfig = false
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.composeCompilerVersion
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

publishing {
    publications {
        getByName<MavenPublication>("AndroidLibrary").apply  {
            artifactId = config.runtimeLibArtifact
            pom {
                name.set("Recomposition Logger runtime")
                description.set("Support library for Recomposition Logger plugin that contains runtime methods")
                url.set("https://github.com/welltech-com/recompose-logger")
            }
        }
    }
}

dependencies {
    compileOnly("androidx.compose.ui:ui-tooling:${Versions.composeToolingVersion}")
    compileOnly(project(":recomposition-logger-annotations"))
}

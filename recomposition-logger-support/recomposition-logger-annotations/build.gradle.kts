plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-publish")
}

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 21
        targetSdk = 31

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

publishing {
    publications {
        getByName<MavenPublication>("AndroidLibrary").apply {
            artifactId = config.annotationsLibArtifact
        }
    }
}

dependencies {
    compileOnly("androidx.compose.ui:ui-tooling:${Versions.composeVersion}")
}
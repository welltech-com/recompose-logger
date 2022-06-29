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
        kotlinCompilerExtensionVersion = Versions.composeVersion
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
        }
    }
}

dependencies {
    compileOnly("androidx.compose.ui:ui-tooling:${Versions.composeVersion}")
    compileOnly(project(":recomposition-logger-annotations"))
}
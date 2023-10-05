buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${config.kotlinVersion}")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }

    project.tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_11.toString()
            freeCompilerArgs = freeCompilerArgs + listOf(
                    "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
                    "-Xopt-in=kotlin.ExperimentalStdlibApi",
                    "-Xopt-in=kotlin.contracts.ExperimentalContracts",
                    "-Xopt-in=kotlin.RequiresOptIn",
                    "-Xjvm-default=all",
            )
        }
    }
}

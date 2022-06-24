buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${pluginConfig.kotlinVersion}")
    }
}

subprojects {
    repositories {
        mavenCentral()
    }
}

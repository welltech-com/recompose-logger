import java.util.Properties
import java.io.*

plugins {
  `kotlin-dsl`
  `kotlin-dsl-precompiled-script-plugins`
}

repositories {
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  implementation(":common-build-scripts")
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${getCommonProperties().getProperty("kotlin_version")}")
  implementation("com.github.gmazzo:gradle-buildconfig-plugin:3.0.3")
}

fun getCommonProperties(): Properties {
  val propsFile = File(rootDir.parentFile.parentFile, "common.properties")
  return Properties().apply {
    load(FileInputStream(propsFile))
  }
}

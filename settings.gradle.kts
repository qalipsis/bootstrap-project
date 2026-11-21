pluginManagement {
    plugins {
        id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    }

    repositories {
        gradlePluginPortal()
    }
}

// Gives Gradle the repository to download JDK 21 from, when it is missing: for the daemon
// (gradle/gradle-daemon-jvm.properties) and for the Kotlin toolchain (build.gradle.kts).
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention")
}

rootProject.name = "bootstrap-project"

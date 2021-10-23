val jvmVersion = System.getenv("jvm") ?: "1.8"
val diskordinVersion: String by extra

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.5.31"
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }

    group = "org.tesserakt.diskordin"
    version = System.getProperty("diskordin.version", diskordinVersion)
    extra["jvmVersion"] = jvmVersion
}

tasks.wrapper {
    gradleVersion = "7.1.1"
}
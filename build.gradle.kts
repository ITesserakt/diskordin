val jvmVersion = System.getenv("jvm") ?: "1.8"
val diskordinVersion: String by extra

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.21"
    maven
}

allprojects {
    repositories {
        jcenter()
        mavenCentral()
        maven("https://kotlin.bintray.com/kotlinx/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://oss.jfrog.org/artifactory/oss-snapshot-local/")
    }

    group = "org.tesserakt.diskordin"
    version = System.getProperty("diskordin.version", diskordinVersion)
    extra["jvmVersion"] = jvmVersion
}

tasks.wrapper {
    gradleVersion = "6.7.1"
}
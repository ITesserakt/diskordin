import org.jetbrains.kotlin.utils.addToStdlib.cast

plugins {
    kotlin("jvm")
}

val arrowVersion: String = project(":").properties["arrow_version"].cast()
val jvmVersion = System.getenv("jvm") ?: "1.8"

fun arrow(module: String, version: String = arrowVersion): Any =
    "io.arrow-kt:arrow-$module:$version"

group = "org.tesserakt.diskordin"
version = "0.3.0"

repositories {
    mavenCentral()
    maven { url = uri("https://oss.jfrog.org/artifactory/oss-snapshot-local/") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation(project(":"))
    implementation(project(":diskordin-commands"))
    implementation(project(":diskordin-retrofit-integration"))

    implementation(arrow("core"))
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = jvmVersion
}

tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = jvmVersion
}

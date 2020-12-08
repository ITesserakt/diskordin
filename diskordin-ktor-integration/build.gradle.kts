import org.jetbrains.kotlin.utils.addToStdlib.cast

plugins {
    kotlin("jvm")
}

val ktorVersion: String by extra
val arrowVersion: String = project(":").properties["arrow_version"].cast()
val jvmVersion = System.getenv("jvm") ?: "14"

fun ktor(module: String, version: String = ktorVersion) =
    "io.ktor:ktor-$module:$version"

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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.0")

    implementation(ktor("client-core"))
    implementation(ktor("client-okhttp"))
    implementation(ktor("client-gson"))

    implementation(arrow("fx-coroutines"))
    implementation(arrow("fx-coroutines-stream"))

    implementation(project(":"))
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = jvmVersion
    kotlinOptions.freeCompilerArgs = listOf("-Xinline-classes")
}

tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = jvmVersion
}
import org.jetbrains.kotlin.utils.addToStdlib.cast

plugins {
    kotlin("jvm")
}


val arrowVersion: String = project(":").properties["arrow_version"].cast()
val jvmVersion = System.getenv("jvm") ?: "1.8"

fun ktor(module: String, version: String = "1.4.1") =
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

    implementation(project(":"))
    implementation(project(":diskordin-commands"))
    implementation(project(":diskordin-ktor-integration"))

    implementation(ktor("client-cio"))
    implementation(ktor("client-logging"))

    implementation(arrow("core"))
    implementation(arrow("mtl"))
    implementation(arrow("fx"))
    implementation(arrow("ui"))

    implementation(group = "org.slf4j", name = "slf4j-api", version = "1.7.30")
    implementation(group = "ch.qos.logback", name = "logback-core", version = "1.2.3")
    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = jvmVersion
}

tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = jvmVersion
}

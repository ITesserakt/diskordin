import org.jetbrains.kotlin.utils.addToStdlib.cast

val kotestVersion: String = project(":").properties["kotest_version"].cast()
val arrowVersion: String = project(":").properties["arrow_version"].cast()
val kotlinLoggingVersion: String = project(":").properties["kotlin_logging_version"].cast()

fun arrow(module: String, version: String = arrowVersion): Any =
    "io.arrow-kt:arrow-$module:$version"

plugins {
    kotlin("jvm")
}

group = "org.tesserakt.diskordin"
version = "0.3.0"

repositories {
    jcenter()
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    maven { url = uri("https://oss.jfrog.org/artifactory/oss-snapshot-local/") }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":"))
    implementation(arrow("core"))
    implementation(arrow("fx"))
    implementation(arrow("mtl"))
    implementation("io.github.classgraph:classgraph:4.8.65")
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
    implementation(group = "org.slf4j", name = "slf4j-api", version = "1.7.30")
    implementation(group = "ch.qos.logback", name = "logback-core", version = "1.2.3")
    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")

    testImplementation(arrow("fx-rx2"))
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-arrow:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    withType<Test> {
        useJUnitPlatform()
    }
}
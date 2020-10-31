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
    implementation(kotlin("reflect"))
    implementation(project(":"))

    implementation(arrow("core"))
    implementation(arrow("fx"))
    implementation(arrow("fx-coroutines"))
    implementation(arrow("mtl"))
    implementation(arrow("ui"))
    implementation(arrow("syntax"))

    implementation("io.github.classgraph:classgraph:4.8.65")
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")

    testImplementation(arrow("fx-rx2"))
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-arrow:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    test {
        useJUnitPlatform()
    }
}
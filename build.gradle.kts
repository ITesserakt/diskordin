@file:Suppress("PropertyName")

val kotlin_version: String by extra
val diskordin_version: String by extra
val coroutines_version: String by extra
val arrow_version: String by extra
val retrofit_version: String by extra
val scarlet_version: String by extra
val kotlin_logging_version: String by extra
val slf4j_version: String by extra
val kotest_version: String by extra

val publicationName = "diskordin"

fun arrow(module: String, version: String = arrow_version): Any =
    "io.arrow-kt:arrow-$module:$version"

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.20"
    id("com.jfrog.bintray") version "1.8.4"
    `maven-publish`
}

group = "org.tesserakt.diskordin"
version = System.getProperty("diskordin.version", diskordin_version)
val jvmVersion = System.getenv("jvm") ?: "1.8"

repositories {
    jcenter()
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    maven { url = uri("https://oss.jfrog.org/artifactory/oss-snapshot-local/") }
}

dependencies {
    implementation(kotlin("stdlib", kotlin_version))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")

    implementation(arrow("syntax"))
    implementation(arrow("fx"))
    implementation(arrow("ui"))
    implementation(arrow("fx-coroutines"))
    implementation(arrow("optics"))

    implementation("com.google.code.gson:gson:2.8.6")

    implementation("io.github.microutils:kotlin-logging:$kotlin_logging_version")

    testImplementation("io.kotest:kotest-runner-junit5:$kotest_version")
    testImplementation("io.kotest:kotest-assertions-arrow:$kotest_version")
    testImplementation("io.kotest:kotest-assertions-core:$kotest_version")
    testImplementation("io.kotest:kotest-property:$kotest_version")
    testImplementation(group = "org.slf4j", name = "slf4j-api", version = "1.7.30")
    testImplementation(group = "ch.qos.logback", name = "logback-core", version = "1.2.3")
    testImplementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")
}

tasks.test {
    useJUnitPlatform()
    failFast = false
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = jvmVersion
        freeCompilerArgs = listOf("-XXLanguage:+InlineClasses", "-Xuse-experimental=kotlin.Experimental")
    }
}
tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = jvmVersion
}

tasks.wrapper {
    gradleVersion = "6.7"
}
val diskordinVersion: String by extra
val coroutinesVersion: String by extra
val arrowVersion: String by extra
val kotlinLoggingVersion: String by extra
val slf4jVersion: String by extra
val kotestVersion: String by extra
val jvmVersion = System.getenv("jvm") ?: "1.8"

val publicationName = "diskordin"

fun arrow(module: String, version: String = arrowVersion): Any =
    "io.arrow-kt:arrow-$module:$version"

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.20"
    id("com.jfrog.bintray") version "1.8.4"
    `maven-publish`
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

dependencies {
    api(kotlin("stdlib"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-datetime", version = "0.1.1")

    implementation(arrow("syntax"))
    implementation(arrow("fx"))
    implementation(arrow("fx-stm"))
    api(arrow("ui"))
    api(arrow("fx-coroutines"))

    implementation("com.google.code.gson:gson:2.8.6")

    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")

    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-arrow:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
    testImplementation(group = "org.slf4j", name = "slf4j-api", version = "1.7.30")
    testImplementation(group = "ch.qos.logback", name = "logback-core", version = "1.2.3")
    testImplementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")
}

tasks.test {
    useJUnitPlatform()
    failFast = false
}

tasks.wrapper {
    gradleVersion = "6.7.1"
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
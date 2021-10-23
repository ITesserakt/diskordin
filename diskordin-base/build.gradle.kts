val diskordinVersion: String by extra
val coroutinesVersion: String by extra
val arrowVersion: String by extra
val kotlinLoggingVersion: String by extra
val slf4jVersion: String by extra
val kotestVersion: String by extra
val jvmVersion = System.getenv("jvm") ?: "1.8"

val publicationName = "diskordin"

fun arrow(module: String, version: String = arrowVersion) =
    "io.arrow-kt:arrow-$module:$version"

plugins {
    kotlin("jvm")
}

group = "org.tesserakt.diskordin"
version = "0.3.0"

repositories {
    mavenCentral()
}

dependencies {
    api(kotlin("stdlib"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-datetime", version = "0.2.1")

    api(arrow("fx-coroutines"))

    implementation("com.google.code.gson:gson:2.8.8")

    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")

    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-assertions-arrow:1.0.3")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
    testImplementation(group = "org.slf4j", name = "slf4j-api", version = slf4jVersion)
    testImplementation(group = "ch.qos.logback", name = "logback-core", version = "1.2.5")
    testImplementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.5")
}

tasks.test {
    useJUnitPlatform()
    failFast = false
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = jvmVersion
        freeCompilerArgs = listOf("-XXLanguage:+InlineClasses", "-Xlambdas=indy")
        useOldBackend = true
    }
}
tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = jvmVersion
}
tasks.kotlinSourcesJar {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

artifacts {
    archives(tasks.kotlinSourcesJar)
}
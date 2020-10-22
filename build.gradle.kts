@file:Suppress("PropertyName")

import com.jfrog.bintray.gradle.BintrayExtension
import java.util.*

val kotlin_version: String = "1.4.10"
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
    id("org.jetbrains.kotlin.jvm") version "1.4.10"
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

    implementation(arrow("core"))
    implementation(arrow("syntax"))
    implementation(arrow("fx"))
    implementation(arrow("integrations-retrofit-adapter"))
    implementation(arrow("ui"))
    implementation(arrow("fx-coroutines"))

    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.squareup.okhttp3:logging-interceptor:4.4.0")
    implementation("com.tinder.scarlet:scarlet:$scarlet_version")
    implementation("com.tinder.scarlet:protocol-websocket-okhttp:$scarlet_version")

    implementation("io.github.microutils:kotlin-logging:$kotlin_logging_version")

    testImplementation("io.kotest:kotest-runner-junit5:$kotest_version")
    testImplementation("io.kotest:kotest-assertions-arrow:$kotest_version")
    testImplementation("io.kotest:kotest-assertions-core:$kotest_version")
    testImplementation("io.kotest:kotest-property:$kotest_version")
}

tasks.test {
    useJUnitPlatform()
    failFast = false
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = jvmVersion
        freeCompilerArgs = listOf("-XXLanguage:+InlineClasses")
    }
}
tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = jvmVersion
}

tasks.wrapper {
    gradleVersion = "6.0.1"
}

tasks.kotlinSourcesJar {
    dependsOn("classes")
    archiveClassifier.convention("source")
    archiveClassifier.set("source")
    from(sourceSets.main.map { it.allSource })
}

tasks.javadoc {
    isFailOnError = false
}

tasks.maybeCreate("javadocJar", Jar::class.java).apply {
    dependsOn("javadoc")
    archiveClassifier.convention("javadoc")
    archiveClassifier.set("javadoc")
    from(tasks.javadoc.map { it.destinationDir!! })
}

publishing {
    println(publications)
    publications.invoke {
        create<MavenPublication>(publicationName) {
            from(components.getByName("java"))

            pom.withXml {
                val root = asNode()
                root.appendNode(
                    "description",
                    "A lightweight wrapper written in Kotlin for Discord API using Arrow"
                )
                root.appendNode("name", "Diskordin")
                root.appendNode("url", "https://github.com/ITesserakt/diskordin")
                root.appendNode("licenses").let {
                    it.appendNode("license").apply {
                        appendNode("name", "The Apache Software License, Version 2.0")
                        appendNode("url", "http://www.apache.org/licenses/LICENSE-2.0.txt")
                        appendNode("distribution", "repo")
                    }
                }
                root.appendNode("developers").let {
                    it.appendNode("developer").apply {
                        appendNode("id", "ITesserakt")
                        appendNode("name", "Nikitin Vladimir")
                        appendNode("email", "potryas66@mail.ru")
                    }
                }
            }
        }
    }
}

bintray {
    user = System.getProperty("bintray.user")
    key = System.getProperty("bintray.key")
    publish = true
    setPublications(publicationName)

    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = "diskordin"
        name = "diskordin"
        setLicenses("Apache-2.0")
        vcsUrl = "https://github.com/ITesserakt/diskordin.git"

        version(delegateClosureOf<BintrayExtension.VersionConfig> {
            name = System.getProperty("diskordin.version", diskordin_version)
            released = Date().toString()
        })
    })
}

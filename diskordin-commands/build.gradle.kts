import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val kotestVersion: String = ext["kotest_version"].safeAs()!!

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
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":"))

    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion") {
        exclude("io.arrow-kt")
    }
    testImplementation("io.kotest:kotest-assertions-arrow:$kotestVersion") {
        exclude("io.arrow-kt")
    }
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    withType<Test> {
        useJUnitPlatform()
    }
}
import org.jetbrains.kotlin.utils.addToStdlib.cast

val kotestVersion: String = project(":").properties["kotest_version"].cast()
val arrowVersion: String = project(":").properties["arrow_version"].cast()
val retrofitVersion: String by extra
val scarletVersion: String by extra
val jvmVersion = System.getenv("jvm") ?: "1.8"

plugins {
    kotlin("jvm")
}

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

    implementation(arrow("fx-coroutines"))

    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:4.4.0")

    implementation("com.tinder.scarlet:scarlet:$scarletVersion")
    implementation("com.tinder.scarlet:protocol-websocket-okhttp:$scarletVersion")
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = jvmVersion
    kotlinOptions.freeCompilerArgs = listOf("-Xinline-classes")
}

tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = jvmVersion
}
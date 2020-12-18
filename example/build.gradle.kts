val arrowVersion: String by extra
val jvmVersion: String by extra

plugins {
    kotlin("jvm")
    application
}

fun ktor(module: String, version: String = "1.4.3") =
    "io.ktor:ktor-$module:$version"

fun arrow(module: String, version: String = arrowVersion): Any =
    "io.arrow-kt:arrow-$module:$version"

dependencies {
    implementation(project(":"))
    implementation(project(":diskordin-commands"))
    implementation(project(":diskordin-retrofit-integration"))

    implementation(ktor("client-cio"))
    implementation(ktor("client-logging"))

    implementation(group = "org.slf4j", name = "slf4j-api", version = "1.7.30")
    implementation(group = "ch.qos.logback", name = "logback-core", version = "1.2.3")
    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")
}

application {
    mainClass.set("MainKt")
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
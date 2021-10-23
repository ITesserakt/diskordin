val arrowVersion: String by extra
val jvmVersion: String by extra
val slf4jVersion: String by extra
val ktorVersion: String by extra

plugins {
    kotlin("jvm")
    application
}

fun ktor(module: String, version: String = ktorVersion) =
    "io.ktor:ktor-$module:$version"

fun arrow(module: String, version: String = arrowVersion): Any =
    "io.arrow-kt:arrow-$module:$version"

dependencies {
    implementation(project(":diskordin-base"))
    implementation(project(":diskordin-commands"))
    implementation(project(":diskordin-ktor-integration"))

    implementation(ktor("client-cio"))
    implementation(ktor("client-logging"))

    implementation(group = "org.slf4j", name = "slf4j-api", version = slf4jVersion)
    implementation(group = "ch.qos.logback", name = "logback-core", version = "1.2.3")
    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")
}

application {
    mainClass.set("MainKt")
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
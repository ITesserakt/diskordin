val ktorVersion: String by extra
val arrowVersion: String by extra
val jvmVersion: String by extra
val kotlinLoggingVersion: String by extra
val coroutinesVersion: String by extra

plugins {
    kotlin("jvm")
}

fun ktor(module: String, version: String = ktorVersion) =
    "io.ktor:ktor-$module:$version"

fun arrow(module: String, version: String = arrowVersion): Any =
    "io.arrow-kt:arrow-$module:$version"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

    implementation(ktor("client-core"))
    implementation(ktor("client-gson"))

    implementation(project(":diskordin-base"))

    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
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
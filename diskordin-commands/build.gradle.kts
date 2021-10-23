val kotestVersion: String by extra
val arrowVersion: String by extra
val kotlinLoggingVersion: String by extra
val jvmVersion: String by extra

plugins {
    kotlin("jvm")
}

fun arrow(module: String, version: String = arrowVersion): Any =
    "io.arrow-kt:arrow-$module:$version"

dependencies {
    implementation(kotlin("reflect"))
    implementation(project(":diskordin-base"))

    implementation("io.github.classgraph:classgraph:4.8.128")
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")

    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-assertions-arrow:1.0.3")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
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
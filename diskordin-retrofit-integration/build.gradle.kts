val kotestVersion: String by extra
val arrowVersion: String by extra
val retrofitVersion: String by extra
val scarletVersion: String by extra
val jvmVersion: String by extra

plugins { kotlin("jvm") }

fun arrow(module: String, version: String = arrowVersion): Any =
    "io.arrow-kt:arrow-$module:$version"

dependencies {
    implementation(project(":"))

    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:4.4.0")

    implementation("com.tinder.scarlet:scarlet:$scarletVersion")
    implementation("com.tinder.scarlet:protocol-websocket-okhttp:$scarletVersion")
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
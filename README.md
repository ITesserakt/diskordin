![Tests status](https://github.com/ITesserakt/diskordin/workflows/Tests/badge.svg)
[![JitPack](https://jitpack.io/v/ITesserakt/diskordin.svg)](https://jitpack.io/#ITesserakt/diskordin)
[![Bintray](https://api.bintray.com/packages/itesserakt/diskordin/diskordin/images/download.svg)](https://bintray.com/itesserakt/diskordin/diskordin/_latestVersion)
[![Github License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)

# Diskordin

## What is this?

Diskordin (**Dis**co**rd** & **Ko**tl**in**) is a [Discord API](https://discordapp.com/developers/docs/) wrapper written
in Kotlin using functional approach which reached with [Arrow](http://arrow-kt.io/) library.
***
The state of this wrapper is **ALPHA** because most features are still in progress. So API can change very fast.
[See the road map](https://github.com/ITesserakt/diskordin/issues/1).
***

## Why I should use it?

There are a lot of other wrappers written in Java (whole 4). Diskordin allows us to write polymorphic programs in
functional style, separating effects from pure functions.

Also, there already been presented other kotlin wrappers. Diskordin gives us flexible builders, and we can choose a
layer of abstraction.

## How can I get it?

#### Gradle

```groovy
repositories {
    maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
    jcenter()
}
dependencies {
    compile 'org.tesserakt.diskordin:diskordin:{version}'
}
```

For snapshot versions use

``` groovy
repositories {
    mavencentral()
    maven { url "https://oss.sonatype.org/content/repositories/snapshots" } // for scarlet (if you're using retrofit-integration module)
    maven { url "https://kotlin.bintray.com/kotlinx/" } // for kotlinx-datetime
    maven { url "https://oss.jfrog.org/artifactory/oss-snapshot-local/" } // for arrow
    maven { url 'https://jitpack.io' } // for this snapshot
}
dependencies {
    // ...
    implementation("com.github.ITesserakt.diskordin:diskordin-base:-SNAPSHOT")
    implementation("com.github.ITesserakt.diskordin:diskordin-ktor-integration:-SNAPSHOT") // use this
    implementation("com.github.ITesserakt.diskordin:diskordin-retrofit-integration:-SNAPSHOT") // OR this, else build errors are guaranteed
    implementation("com.github.ITesserakt.diskordin:diskordin-commands:-SNAPSHOT")
}
```

**Also, you must add a dependency for logger. Simplest is**
`'org.slf4j:slf4j-simple:1.7.30'`

## How can I use it?

**The future syntax may change!**

#### Log on

Just logging into Discord as simple as possible

```kotlin
fun main() = DiscordClientBuilder {
    +token("Put your bot`s token here")
}.login().unsafeRunSync()
```

In DiscordClientBuilder lambda you can put other config options. Like other builders, type `this.` keyword in a lambda
to see all of them.

If your token is in env variables the syntax will even more simple:

 ```kotlin
fun main() = DiscordClientBuilder().login().unsafeRunSync()
```

Most wrappers block main forever after `login`. In Diskordin `login` call is non-blocking, but you shouldn't
pass `delay(INFINITY)` or something else. Wrapper creates another thread, so the program doesn't terminate.

Here is an example where you can see work with the Gateway. Currently, a type of `readyEvent` is Kind<ForFlowable,
ReadyEvent> because of limitations in the Arrow. To repair it, use `fix` and `flowable` after it. In the future, this
will change to S\<ReadyEvent\> where `S` is a concrete "streamable" type of the Gateway.

 ```kotlin
 fun main() = with(DiscordClientBuilder()) {
    login().unsafeRunSync()
    val readyEvents = eventDispatcher.subscribeOn()
    // ...
}
```

#### Builders

In some functions like `edit`, you can meet with `builders`. These are state mutators, necessary parameters of which
are "inlined" into the corresponding function. In a lambda of the builder, you can apply optional properties. You _
apply_ them and so you have to _bind_ them with state. For this, there is a unary + operator.

```kotlin
//An example of ITextChannel.edit
channel.edit { //this is TextChannelEditBuilder
    +name("New fancy name")
    +topic("New fancy topic!")
    // ...
}
```

Auto-complete helps you to show all optional properties
***

Here will more use cases as they will implement.

### Libraries

**Libraries used in main module:**

| Name                                                              | Reason                                                         |
| ----------------------------------------------------------------- | -------------------------------------------------------------- |
| [Arrow](https://github.com/arrow-kt/arrow)                        | Functional approach in Kotlin                                  |
| [Kotlin logging](https://github.com/MicroUtils/kotlin-logging)    | Kotlin adapter for loggers                                     |
| [Kotest](https://github.com/kotest/kotest)                        | Powerful, elegant and flexible test framework for Kotlin       |

**Libraries used in `diskordin-commands`:**

| Name | Reason |
| ---- | ------ |
| [ClassGraph](https://github.com/classgraph/classgraph) | An uber-fast, ultra-lightweight, parallelized Java classpath scanner and module scanner. |
| Kotlin reflection | To access data, produced by ClassGraph |

**Libraries used in `diskordin-ktor-integration`:**

| Name | Reason |
| ---- | ------ |
| [Ktor](https://github.com/ktorio/ktor) | Framework for quickly creating connected applications in Kotlin with minimal effort |

**Libraries used in `diskordin-retrofit-integration`:**

| Name | Reason |
| ---- | ------ |
| [Retrofit](https://github.com/square/retrofit) | A type-safe HTTP client for Android and the JVM |
| [Scarlet](https://github.com/Tinder/Scarlet) | A Retrofit inspired WebSocket client for Kotlin, Java, and Android |

[![](https://jitci.com/gh/ITesserakt/diskordin/svg)](https://jitci.com/gh/ITesserakt/diskordin) 
[![](https://jitpack.io/v/ITesserakt/diskordin.svg)](https://jitpack.io/#ITesserakt/diskordin)

# Diskordin
## What is this?
Diskordin (**Dis**co**rd** & **Ko**tl**in**) is a [Discord API](https://discordapp.com/developers/docs/) wrapper written in Kotlin using
functional approach which reached with [Arrow](http://arrow-kt.io/) library.
***
The state of this wrapper is **PRE-ALPHA** because most features are not implemented. 
So API can change very fast.
[See road map](https://github.com/ITesserakt/diskordin/issues/1).
***
## Why I should use it?
There are a lot of other wrappers written in Java (whole 4). 
But Diskordin allows us to write polymorphic programs in functional style, separating effects from pure functions 

Also, there already been presented other kotlin wrappers. 
Diskordin gives us flexible builders, and we can choose a layer of abstraction.
## How can I get it?
#### Gradle
```groovy
repositories {
    jcenter()
    maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
    maven { url 'https://jitpack.io' }
}
dependencies {
    implementation 'com.github.ITesserakt:diskordin:0.1.1'
}
```
#### Maven
```xml
<repositories>
    <repository>
   <id>jitpack.io</id>
   <url>https://jitpack.io</url>
    </repository>
</repositories>
```
```xml
<dependencies>
    <dependency>
   <groupId>com.github.ITesserakt</groupId>
   <artifactId>diskordin</artifactId>
   <version>0.1.1</version>
    </dependency>
</dependencies>
```
#### SBT 
```scala
resolvers += "jitpack" at "https://jitpack.io"
libraryDependencies += "com.github.ITesserakt" % "diskordin" % "0.1.1"
```
**Also you must add a dependency for logger. The most simple is** 
`'org.slf4j:slf4j-simple:1.7.26'`
## How can I use it? 
**The future syntax may change!**

Just logging into Discord as simple as possible
```kotlin
    fun main() = DiscordClientBuilder {
        token = "Put your bot`s token here"
    }.login()
```
In DiscordClientBuilder lambda you can put other config options.
 Like other builders, type `this.` keyword in a lambda to see all of them.
 
 If your token is in env variables the syntax will even more simple:
 ```kotlin
    fun main() = DiscordClientBuilder{}.login()
```

Most wrappers block main forever after `login`.
In Diskordin vice versa subscribing to different events from Discord are after `client.login` call.
 ```kotlin
    fun main() {
        val client = DiscordClientBuilder {}
        client.login()
        client.eventDispatcher.subscribeOn<ReadyEvent>()
            .collect { println(it) } //subscribeOn returns Flow
    }
```
There will more use cases as they will implement.

### Libraries
|Name               | Reason                                                        |
| ----------------- | ------------------------------------------------------------- |
| Arrow             | Functional approach in Kotlin                                 |
| Kotlin coroutines | Dependency to Arrow. Gateway events structure                 |
| Koin              | Dependency injection library                                  |
| OkHTTP            | Http client implementation on which Rest and Gateway are based|
| Scarlet           | Easy work with WebSockets                                     |
| Kotlin logging    | Kotlin adapter for loggers                                    |
| JUnit             | Tests                                                         |
| Kluent            | Extensions providing easily unit testing                      |


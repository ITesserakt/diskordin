# Diskordin
### What is this?
Diskordin (**Dis**co**rd** & **Ko**tl**in**) is a [Discord API](https://discordapp.com/developers/docs/) wrapper written in Kotlin using
functional approach which reached with [Arrow](http://arrow-kt.io/) library.
***
The state of this wrapper is **PRE-ALPHA** because most features are not implemented. 
So API can change very fast.
[See road map](https://github.com/ITesserakt/diskordin/issues/1).
***
### Why I should use it?
There are a lot of other wrappers written in Java (whole 4). 
But Diskordin allows us to write polymorphic programs in functional style, separating effects from pure functions 

Also, there already been presented other kotlin wrappers. 
Diskordin gives to us flexible builders, and we can choose which layer of abstraction choose.
### How can I use it? 
**The future syntax may change!**

Just logging into Discord as simple as possible
```kotlin
    fun main() = DiscordClientBuilder {
        token = "Put your bot`s token here"
    }.login()
```
In DiscordClientBuilder lambda you can put other config options.
 Like other builders, type `this.` keyword in lambda to see all of they.
 
 If your token are in env variables the syntax will even more simple:
 ```kotlin
    fun main() = DiscordClientBuilder{}.login()
```

Most wrappers blocks main forever after `login`.
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

#### Libraries
|Name               | Reason                                                        |
| ----------------- | ------------------------------------------------------------- |
| Arrow             | Functional approach in Kotlin                                 |
| Kotlin coroutines | Dependency to Arrow. Gateway events structure                 |
| Koin              | Dependency injection library                                  |
| OkHTTP            | Http client implementation on which Rest and Gateway are based|
| Scarlet           | Easy work with websockets                                     |
| SLF4J             | Logging                                                       |
| JUnit             | Tests                                                         |
| Kluent            | Extensions providing easily unit testing                      |


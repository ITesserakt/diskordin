package ru.tesserakt.diskordin.core.client

import org.kodein.di.Kodein

object Diskordin {
    private const val API_VERSION = "6"
    internal const val API_URL = "https://discordapp.com/api/v$API_VERSION"
    const val LIB_VERSION = "0.0.1"

    val kodein = Kodein {
        import(DiscordClientBuilder.kodein)
    }
}
package org.tesserakt.diskordin.rest.integration

import org.tesserakt.diskordin.impl.core.client.DiscordClientBuilder
import org.tesserakt.diskordin.rest.KtorRestClient

suspend fun DiscordClientBuilder.RestBuildPhase.ktorBackend() = defineRestBackend {
    val ktor by KtorService(httpClient)
    KtorRestClient(ktor, discordApiURL, schedule)
}
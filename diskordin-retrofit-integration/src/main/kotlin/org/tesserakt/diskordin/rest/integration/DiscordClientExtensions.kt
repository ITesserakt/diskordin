package org.tesserakt.diskordin.rest.integration

import org.tesserakt.diskordin.impl.core.client.DiscordClientBuilder
import org.tesserakt.diskordin.rest.RestClient
import org.tesserakt.diskordin.rest.byRetrofit

suspend fun DiscordClientBuilder.RestBuildPhase.retrofitBackend() = defineRestBackend {
    val retrofit by RetrofitService(httpClient, discordApiURL)
    RestClient.byRetrofit(retrofit, schedule)
}
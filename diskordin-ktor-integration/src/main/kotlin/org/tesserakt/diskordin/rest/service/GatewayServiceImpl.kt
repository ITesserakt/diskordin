package org.tesserakt.diskordin.rest.service

import arrow.core.Id
import io.ktor.client.*
import io.ktor.client.request.*
import org.tesserakt.diskordin.core.data.json.response.GatewayBotResponse

class GatewayServiceImpl(private val ktor: HttpClient, private val discordApiUrl: String) : GatewayService {
    override suspend fun getGatewayBot(): Id<GatewayBotResponse> = ktor.get("$discordApiUrl/api/v6/gateway/bot")
}
package org.tesserakt.diskordin.rest.service

import arrow.core.Id
import org.tesserakt.diskordin.core.data.json.response.GatewayBotResponse
import retrofit2.http.GET

interface GatewayService {
    @GET("/api/v6/gateway/bot")
    suspend fun getGatewayBot(): Id<GatewayBotResponse>
}
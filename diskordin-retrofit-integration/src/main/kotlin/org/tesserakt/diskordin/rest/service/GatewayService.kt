package org.tesserakt.diskordin.rest.service

import org.tesserakt.diskordin.core.data.json.response.GatewayBotResponse
import retrofit2.http.GET

interface GatewayServiceImpl : GatewayService {
    @GET("/api/v6/gateway/bot")
    override suspend fun getGatewayBot(): GatewayBotResponse
}
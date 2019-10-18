package ru.tesserakt.diskordin.rest.service

import retrofit2.http.GET
import ru.tesserakt.diskordin.core.data.json.response.GatewayBotResponse

interface GatewayService {
    @GET("/api/v6/gateway/bot")
    suspend fun getGatewayBot(): GatewayBotResponse
}
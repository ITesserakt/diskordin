package ru.tesserakt.diskordin.rest.service

import arrow.core.Id
import arrow.integrations.retrofit.adapter.CallK
import retrofit2.http.GET
import ru.tesserakt.diskordin.core.data.json.response.GatewayBotResponse

interface GatewayService {
    @GET("/api/v6/gateway/bot")
    fun getGatewayBot(): CallK<Id<GatewayBotResponse>>
}
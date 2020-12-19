package org.tesserakt.diskordin.rest.service

import org.tesserakt.diskordin.core.data.json.response.GatewayBotResponse

interface GatewayService {
    suspend fun getGatewayBot(): GatewayBotResponse
}
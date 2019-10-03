package ru.tesserakt.diskordin.rest.resource

import ru.tesserakt.diskordin.core.data.json.response.GatewayBotResponse
import ru.tesserakt.diskordin.core.data.json.response.GatewayResponse
import ru.tesserakt.diskordin.rest.Routes

internal object GatewayResource {
    object General {
        suspend fun getGatewayURL() =
            Routes.getGateway()
                .newRequest()
                .resolve<GatewayResponse>()

        suspend fun getGatewayBot() =
            Routes.getGatewayBot()
                .newRequest()
                .resolve<GatewayBotResponse>()
    }
}
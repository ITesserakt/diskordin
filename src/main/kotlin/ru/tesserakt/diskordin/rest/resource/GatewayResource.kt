package ru.tesserakt.diskordin.rest.resource

import ru.tesserakt.diskordin.rest.Routes

internal object GatewayResource {
    object General {
        suspend fun getGatewayURL() =
            Routes.getGateway()
                .newRequest()
                .resolve()

        suspend fun getGatewayBot() =
            Routes.getGatewayBot()
                .newRequest()
                .resolve()
    }
}
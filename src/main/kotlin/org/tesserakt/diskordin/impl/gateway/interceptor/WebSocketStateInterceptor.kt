package org.tesserakt.diskordin.impl.gateway.interceptor

import mu.KotlinLogging
import org.tesserakt.diskordin.gateway.interceptor.TokenInterceptor
import org.tesserakt.diskordin.gateway.json.IToken
import org.tesserakt.diskordin.gateway.json.token.ConnectionClosed
import org.tesserakt.diskordin.gateway.json.token.ConnectionFailed
import org.tesserakt.diskordin.gateway.json.token.ConnectionOpened

class WebSocketStateInterceptor : TokenInterceptor() {
    private val logger = KotlinLogging.logger("[Gateway]")

    override fun intercept(context: Context) {
        logStateUpdates(context.token)
    }

    private fun logStateUpdates(state: IToken) = when (state) {
        is ConnectionOpened -> logger.info("Gateway reached")
        is ConnectionClosed -> logger.warn("Gateway closed: ${state.reason}")
        is ConnectionFailed -> logger.error("Gateway met with error: ${state.error}")
        else -> Unit
    }
}
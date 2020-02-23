package org.tesserakt.diskordin.impl.gateway.interceptor

import mu.KotlinLogging
import org.tesserakt.diskordin.gateway.interceptor.TokenInterceptor
import org.tesserakt.diskordin.gateway.json.IToken
import org.tesserakt.diskordin.gateway.json.token.ConnectionClosed
import org.tesserakt.diskordin.gateway.json.token.ConnectionFailed
import org.tesserakt.diskordin.gateway.json.token.ConnectionOpened
import org.tesserakt.diskordin.gateway.shard.Shard

class WebSocketStateInterceptor : TokenInterceptor() {
    private val logger = KotlinLogging.logger("[Gateway]")

    override suspend fun intercept(context: Context) = context.run {
        logStateUpdates(context.token)

        if (context.token is ConnectionFailed && context.shard.isReady())
            context.controller.resumeShard(context.shard)

        when (context.token) {
            is ConnectionOpened -> shard.state = Shard.State.Connecting
            is ConnectionFailed, is ConnectionClosed -> shard.state = Shard.State.Disconnected
        }
    }

    private fun Context.logStateUpdates(state: IToken) = when (state) {
        is ConnectionOpened -> logger.info("Shard #${shard.shardData.current} reached")
        is ConnectionClosed -> logger.warn("Shard #${shard.shardData.current} closed: ${state.reason}")
        is ConnectionFailed -> logger.error(
            "Shard #${shard.shardData.current} met with error:", state.error
        )
        else -> Unit
    }
}
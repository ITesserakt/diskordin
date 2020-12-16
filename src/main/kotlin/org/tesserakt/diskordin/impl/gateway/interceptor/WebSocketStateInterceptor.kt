package org.tesserakt.diskordin.impl.gateway.interceptor

import kotlinx.coroutines.ExperimentalCoroutinesApi
import mu.KotlinLogging
import org.tesserakt.diskordin.gateway.interceptor.TokenInterceptor
import org.tesserakt.diskordin.gateway.json.IToken
import org.tesserakt.diskordin.gateway.json.token.ConnectionClosed
import org.tesserakt.diskordin.gateway.json.token.ConnectionClosing
import org.tesserakt.diskordin.gateway.json.token.ConnectionFailed
import org.tesserakt.diskordin.gateway.json.token.ConnectionOpened
import org.tesserakt.diskordin.gateway.shard.Shard

class WebSocketStateInterceptor : TokenInterceptor() {
    private val logger = KotlinLogging.logger("[Gateway]")

    @ExperimentalCoroutinesApi
    override suspend fun intercept(context: Context) = context.run {
        logStateUpdates(context.token)

        if (context.token is ConnectionFailed && context.shard.isReady())
            context.controller.resumeShard(context.shard)

        when (context.token) {
            is ConnectionOpened -> shard._state.value = Shard.State.Connecting
            is ConnectionFailed, is ConnectionClosed -> shard._state.value = Shard.State.Disconnected
        }
    }

    private fun Context.logStateUpdates(state: IToken) = when (state) {
        is ConnectionOpened -> logger.info("Shard #${shard.shardData.index} reached")
        is ConnectionClosing -> logger.warn("Shard #${shard.shardData.index} closing: ${state.reason}")
        is ConnectionFailed -> logger.error("Shard #${shard.shardData.index} met with error:", state.error)
        is ConnectionClosed -> logger.warn("Shard #${shard.shardData.index} closed")
        else -> Unit
    }
}
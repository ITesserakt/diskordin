package org.tesserakt.diskordin.impl.gateway.interceptor

import arrow.fx.typeclasses.Async
import mu.KotlinLogging
import org.tesserakt.diskordin.gateway.interceptor.TokenInterceptor
import org.tesserakt.diskordin.gateway.json.IToken
import org.tesserakt.diskordin.gateway.json.token.ConnectionClosed
import org.tesserakt.diskordin.gateway.json.token.ConnectionFailed
import org.tesserakt.diskordin.gateway.json.token.ConnectionOpened
import org.tesserakt.diskordin.gateway.shard.Shard

class WebSocketStateInterceptor<F>(private val A: Async<F>) : TokenInterceptor<F>() {
    private val logger = KotlinLogging.logger("[Gateway]")

    override fun intercept(context: Context) = context.run {
        A.fx.async {
            !effect { logStateUpdates(context.token) }

            if (context.token is ConnectionFailed && context.shard.isReady())
                !effect { context.controller.resumeShard(context.shard) }

            when (context.token) {
                is ConnectionOpened -> if (shard.state == Shard.State.Disconnected)
                    shard.state = Shard.State.Connecting
                is ConnectionFailed -> shard.state = Shard.State.Disconnected
                is ConnectionClosed -> if (shard.state != Shard.State.Invalidated)
                    shard.state = Shard.State.Disconnected
            }
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
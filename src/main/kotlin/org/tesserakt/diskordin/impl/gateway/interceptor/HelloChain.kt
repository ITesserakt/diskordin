package org.tesserakt.diskordin.impl.gateway.interceptor

import kotlinx.coroutines.delay
import org.tesserakt.diskordin.core.data.event.lifecycle.HelloEvent
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor
import org.tesserakt.diskordin.gateway.interceptor.TokenInterceptor
import org.tesserakt.diskordin.gateway.json.IToken
import org.tesserakt.diskordin.gateway.json.commands.Heartbeat
import org.tesserakt.diskordin.gateway.json.token.ConnectionFailed
import org.tesserakt.diskordin.gateway.json.token.ConnectionOpened
import org.tesserakt.diskordin.gateway.json.token.NoConnection
import java.time.Duration
import java.time.Instant

class HelloChain : EventInterceptor() {
    private var webSocketState: IToken = NoConnection
    private var lastWSErrorTime = Instant.MIN

    private fun isErrorHappened(from: Instant, to: Instant) =
        lastWSErrorTime in from..to

    override suspend fun Context.hello(event: HelloEvent) {
        val interval = event.heartbeatInterval
        controller.openShard(shardIndex)

        while (webSocketState is ConnectionOpened) {
            sendPayload(Heartbeat(sequenceId()))
            delay(interval)

            val now = Instant.now()
            //because while delay gateway may restart and this heartbeat will continue
            if (isErrorHappened(now - Duration.ofMillis(interval), now))
                break
        }
    }

    inner class ConnectionInterceptor : TokenInterceptor() {
        override suspend fun intercept(context: Context) {
            webSocketState = context.token
            if (context.token is ConnectionFailed)
                lastWSErrorTime = Instant.now()
        }
    }
}
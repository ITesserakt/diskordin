package org.tesserakt.diskordin.impl.gateway.interceptor

import kotlinx.coroutines.delay
import org.tesserakt.diskordin.core.data.event.lifecycle.HelloEvent
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor
import org.tesserakt.diskordin.gateway.interceptor.TokenInterceptor
import org.tesserakt.diskordin.gateway.json.IToken
import org.tesserakt.diskordin.gateway.json.commands.Heartbeat
import org.tesserakt.diskordin.gateway.json.token.ConnectionOpened
import org.tesserakt.diskordin.gateway.json.token.NoConnection

class HelloChain : EventInterceptor() {
    private var webSocketState: IToken = NoConnection

    override suspend fun Context.hello(event: HelloEvent) {
        val interval = event.heartbeatInterval
        controller.openShard(0)

        while (webSocketState is ConnectionOpened) {
            sendPayload(Heartbeat(sequenceId()))
            delay(interval)
        }
    }

    inner class ConnectionInterceptor : TokenInterceptor() {
        override suspend fun intercept(context: Context) {
            webSocketState = context.token
        }
    }
}
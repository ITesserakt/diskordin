package org.tesserakt.diskordin.gateway

import arrow.fx.coroutines.stream.Stream
import arrow.fx.coroutines.stream.callback
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.tesserakt.diskordin.gateway.json.Message
import org.tesserakt.diskordin.gateway.json.WebSocketEvent

class GatewayConnectionImpl(private val session: DefaultClientWebSocketSession) : GatewayConnection {
    override fun send(data: Message) = runBlocking {
        session.send(data.decompress())
    }

    override fun receive(): Stream<WebSocketEvent> = Stream.callback {
        session.incoming.consumeAsFlow()
            .onStart { emit(WebSocketEvent.ConnectionOpened) }
            .map(FrameTransformer::transform)
            .onEach(::emit)
            .onCompletion { end() }
            .catch { WebSocketEvent.ConnectionFailed(it) }
            .collect()
    }
}
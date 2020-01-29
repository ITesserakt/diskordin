package org.tesserakt.diskordin.gateway.interceptor

import com.tinder.scarlet.Message
import com.tinder.scarlet.websocket.WebSocketEvent
import org.tesserakt.diskordin.gateway.json.IPayload
import org.tesserakt.diskordin.gateway.json.IRawEvent
import org.tesserakt.diskordin.gateway.json.Payload
import org.tesserakt.diskordin.gateway.json.token.ConnectionClosed
import org.tesserakt.diskordin.gateway.json.token.ConnectionClosing
import org.tesserakt.diskordin.gateway.json.token.ConnectionFailed
import org.tesserakt.diskordin.gateway.json.token.ConnectionOpened
import org.tesserakt.diskordin.util.fromJson
import org.tesserakt.diskordin.util.toJsonTree
import java.io.ByteArrayOutputStream
import java.util.zip.InflaterInputStream

object WebSocketEventTransformer : Transformer<WebSocketEvent, Payload<out IPayload>> {
    override fun transform(context: WebSocketEvent) = when (context) {
        is WebSocketEvent.OnConnectionOpened -> Payload<ConnectionOpened>(
            -1,
            null,
            "CONNECTION_OPENED",
            ConnectionOpened.toJsonTree()
        )
        is WebSocketEvent.OnMessageReceived -> parseTextMessage(context.message).fromJson<Payload<IRawEvent>>()
        is WebSocketEvent.OnConnectionClosing -> Payload<ConnectionClosing>(
            -1,
            null,
            "CONNECTION_CLOSING",
            ConnectionClosing(context.shutdownReason).toJsonTree()
        )
        is WebSocketEvent.OnConnectionClosed -> Payload<ConnectionClosed>(
            -1,
            null,
            "CONNECTION_CLOSED",
            ConnectionClosed(context.shutdownReason).toJsonTree()
        )
        is WebSocketEvent.OnConnectionFailed -> Payload<ConnectionFailed>(
            -1,
            null,
            "CONNECTION_FAILED",
            ConnectionFailed(context.throwable).toJsonTree()
        )
    }

    private fun decompressFromZLib(input: ByteArray): String {
        val inflater = InflaterInputStream(input.inputStream())
        val output = ByteArrayOutputStream()
        val buffer = ByteArray(1024)

        while (true) {
            val len = inflater.read(buffer)
            if (len <= 0) break
            output.write(buffer, 0, len)
        }

        output.close()
        inflater.close()

        return output.toString(Charsets.UTF_8.name())
    }

    private fun parseTextMessage(message: Message): String = when (message) {
        is Message.Text -> message.value
        is Message.Bytes -> decompressFromZLib(message.value)
    }
}
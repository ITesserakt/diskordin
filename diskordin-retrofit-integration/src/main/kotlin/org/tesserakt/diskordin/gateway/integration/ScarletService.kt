package org.tesserakt.diskordin.gateway.integration

import arrow.core.Eval
import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.Protocol
import com.tinder.scarlet.ProtocolSpecificEventAdapter
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.retry.BackoffStrategy
import com.tinder.scarlet.websocket.ShutdownReason
import com.tinder.scarlet.websocket.okhttp.OkHttpWebSocket
import okhttp3.OkHttpClient
import okhttp3.Request
import org.tesserakt.diskordin.gateway.MessageAdapter
import org.tesserakt.diskordin.gateway.WebSocketEventAdapter
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ScarletService(
    httpClient: Eval<OkHttpClient>,
    private val gatewayUrl: String,
    private val lifecycle: Lifecycle,
    private val backoffStrategy: BackoffStrategy,
    private val debug: Boolean
) : ReadOnlyProperty<Nothing?, Eval<Scarlet>> {
    private val protocol = httpClient.map { OkHttpProtocolWrapper(it, gatewayUrl) }

    override fun getValue(thisRef: Nothing?, property: KProperty<*>): Eval<Scarlet> = protocol.map {
        val config = Scarlet.Configuration(
            lifecycle,
            backoffStrategy,
            emptyList(),
            listOf(MessageAdapter.Factory),
            debug
        )
        Scarlet(it, config)
    }

    private class OkHttpProtocolWrapper(private val httpClient: OkHttpClient, private val gatewayUrl: String) :
        Protocol by OkHttpWebSocket(httpClient, OkHttpWebSocket.SimpleRequestFactory(
            { Request.Builder().url(gatewayUrl).build() },
            { ShutdownReason.GRACEFUL }
        )) {
        override fun createEventAdapterFactory(): ProtocolSpecificEventAdapter.Factory = WebSocketEventAdapter.Factory
    }
}
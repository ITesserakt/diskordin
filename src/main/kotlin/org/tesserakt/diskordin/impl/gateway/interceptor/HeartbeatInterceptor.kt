package org.tesserakt.diskordin.impl.gateway.interceptor

import arrow.Kind
import arrow.fx.typeclasses.Concurrent
import org.tesserakt.diskordin.core.data.event.lifecycle.HeartbeatEvent
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor
import org.tesserakt.diskordin.gateway.json.commands.Heartbeat

class HeartbeatInterceptor<F>(CC: Concurrent<F>) : EventInterceptor<F>(CC) {
    override fun Context.heartbeat(event: HeartbeatEvent): Kind<F, Unit> =
        sendPayload(Heartbeat(event.v))
}
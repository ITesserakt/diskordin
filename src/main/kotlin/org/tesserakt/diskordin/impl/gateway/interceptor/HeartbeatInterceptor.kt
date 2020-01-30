package org.tesserakt.diskordin.impl.gateway.interceptor

import org.tesserakt.diskordin.core.data.event.lifecycle.HeartbeatEvent
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor
import org.tesserakt.diskordin.gateway.json.commands.Heartbeat

class HeartbeatInterceptor(private val sequenceId: () -> Int?) : EventInterceptor() {
    override suspend fun Context.heartbeat(event: HeartbeatEvent) {
        sendPayload(Heartbeat(event.v), sequenceId())
    }
}
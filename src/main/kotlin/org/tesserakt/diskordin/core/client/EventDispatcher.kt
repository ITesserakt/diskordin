package org.tesserakt.diskordin.core.client

import kotlinx.coroutines.flow.Flow
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.gateway.json.IRawEvent
import org.tesserakt.diskordin.gateway.json.Payload
import org.tesserakt.diskordin.gateway.json.commands.GatewayCommand

abstract class EventDispatcher {
    internal abstract suspend fun publish(rawEvent: Payload<IRawEvent>)
    abstract fun <E : IEvent> subscribeOn(type: Class<E>): Flow<E>
    abstract fun sendAnswer(payload: GatewayCommand): Boolean
    inline fun <reified E : IEvent> subscribeOn() = subscribeOn(E::class.java)
}
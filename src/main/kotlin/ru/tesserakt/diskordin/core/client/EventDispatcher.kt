package ru.tesserakt.diskordin.core.client

import kotlinx.coroutines.flow.Flow
import ru.tesserakt.diskordin.core.data.event.IEvent
import ru.tesserakt.diskordin.gateway.json.IGatewayCommand
import ru.tesserakt.diskordin.gateway.json.IRawEvent
import ru.tesserakt.diskordin.gateway.json.Payload

abstract class EventDispatcher {
    internal abstract suspend fun publish(rawEvent: Payload<IRawEvent>)
    abstract fun <E : IEvent> subscribeOn(type: Class<E>): Flow<E>
    abstract fun sendAnswer(payload: IGatewayCommand): Boolean
    inline fun <reified E : IEvent> subscribeOn() = subscribeOn(E::class.java)
}
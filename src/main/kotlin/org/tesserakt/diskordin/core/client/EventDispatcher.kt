package org.tesserakt.diskordin.core.client

import arrow.Kind
import arrow.core.Either
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.gateway.json.IRawEvent
import org.tesserakt.diskordin.gateway.json.Payload

abstract class EventDispatcher<F> {
    internal abstract fun publish(rawEvent: Payload<IRawEvent>): Either<ParseError, IEvent>
    abstract fun <E : IEvent> subscribeOn(type: Class<E>): Kind<F, E>
    inline fun <reified E : IEvent> subscribeOn() = subscribeOn(E::class.java)

    sealed class ParseError(val message: String) {
        object NonExistentOpcode : ParseError("Only send opcodes are suitable")
        data class NonExistentDispatch(private val rawEvent: Payload<IRawEvent>) :
            ParseError("No such event name or opcode: ${rawEvent.opcode}, ${rawEvent.name}")

        data class Unknown(private val cause: Throwable) : ParseError(cause.message ?: cause.localizedMessage)
    }
}
package org.tesserakt.diskordin.core.client

import arrow.fx.rx2.ObservableK
import arrow.fx.rx2.extensions.fx
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.amshove.kluent.`should be instance of`
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldThrow
import org.junit.jupiter.api.Test
import org.tesserakt.diskordin.core.data.event.IEvent
import org.tesserakt.diskordin.core.data.event.lifecycle.HeartbeatEvent
import org.tesserakt.diskordin.core.data.event.lifecycle.HelloEvent
import org.tesserakt.diskordin.gateway.json.IRawEvent
import org.tesserakt.diskordin.gateway.json.Opcode
import org.tesserakt.diskordin.gateway.json.Payload
import org.tesserakt.diskordin.gateway.json.asInt
import org.tesserakt.diskordin.gateway.json.commands.Heartbeat
import org.tesserakt.diskordin.gateway.json.events.Hello
import org.tesserakt.diskordin.getLeft
import org.tesserakt.diskordin.getRight
import org.tesserakt.diskordin.impl.core.client.EventDispatcherImpl
import org.tesserakt.diskordin.impl.util.typeclass.observablek.generative.generative
import org.tesserakt.diskordin.util.toJsonTree

@ExperimentalCoroutinesApi
internal class EventDispatcherTest {
    private val eventDispatcher = EventDispatcherImpl(ObservableK.generative())

    private val hello = Payload<IRawEvent>(
        Opcode.HELLO.asInt(),
        null,
        "HELLO",
        Hello(40000, emptyArray()).toJsonTree()
    )

    private val heartbeat = Payload<IRawEvent>(
        Opcode.HEARTBEAT.asInt(),
        null,
        "HEARTBEAT",
        Heartbeat(null).toJsonTree()
    )

    private val corruptedData = Payload<IRawEvent>(
        Opcode.INVALID_SESSION.asInt(),
        null,
        "INVALID_SESSION",
        Heartbeat(null).toJsonTree()
    )

    private val otherOpcode = Payload<IRawEvent>(
        Opcode.RESUME.asInt(),
        null,
        "RESUME",
        null
    )

    @Test
    fun `sent events should appear in dispatcher`() = ObservableK.fx {
        eventDispatcher.publish(hello).isRight().shouldBeTrue()
        val event = !eventDispatcher.subscribeOn<IEvent>()
        event `should be instance of` HelloEvent::class

        eventDispatcher.publish(heartbeat).isRight().shouldBeTrue()
        val secondEvent = !eventDispatcher.subscribeOn<IEvent>()
        secondEvent `should be instance of` HeartbeatEvent::class
    }.observable.subscribe().run { Unit }

    @Test
    fun `corrupted payload should produce error`() {
        { eventDispatcher.publish(corruptedData).getRight() } shouldThrow JsonSyntaxException::class
        eventDispatcher.publish(otherOpcode).getLeft() `should be instance of` EventDispatcher.ParseError.NonExistentOpcode::class
    }
}
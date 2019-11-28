package org.tesserakt.diskordin.gateway

import arrow.core.ListK
import arrow.core.extensions.listk.applicative.applicative
import arrow.core.extensions.monoid
import arrow.core.fix
import arrow.fx.rx2.FlowableK
import arrow.fx.rx2.ObservableK
import arrow.fx.rx2.extensions.flowablek.applicative.applicative
import arrow.fx.rx2.extensions.flowablek.foldable.size
import arrow.fx.rx2.extensions.observablek.applicative.applicative
import arrow.fx.rx2.extensions.observablek.foldable.size
import arrow.fx.rx2.fix
import com.tinder.scarlet.Message
import com.tinder.scarlet.Stream
import com.tinder.scarlet.utils.FlowableStream
import com.tinder.scarlet.websocket.ShutdownReason
import com.tinder.scarlet.websocket.WebSocketEvent
import io.mockk.mockk
import io.reactivex.Flowable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.amshove.kluent.`should contain same`
import org.amshove.kluent.`should equal`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.tesserakt.diskordin.gateway.interpreter.Implementation
import org.tesserakt.diskordin.gateway.interpreter.flowableInterpreter
import org.tesserakt.diskordin.gateway.interpreter.listKInterpreter
import org.tesserakt.diskordin.gateway.interpreter.observableInterpreter
import org.tesserakt.diskordin.gateway.json.Payload
import org.tesserakt.diskordin.gateway.json.commands.GatewayCommand
import org.tesserakt.diskordin.gateway.json.events.Hello
import org.tesserakt.diskordin.util.toJson
import org.tesserakt.diskordin.util.toJsonTree
import kotlin.time.ExperimentalTime

internal class GatewayTest {
    lateinit var buffer: MutableList<Any>
    lateinit var innerFlowable: Flowable<WebSocketEvent>
    private lateinit var impl: Implementation

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    val gateway = Gateway()

    @BeforeEach
    internal fun setUp() {
        buffer = mutableListOf()
        innerFlowable = Flowable.fromArray(
            WebSocketEvent.OnConnectionOpened(mockk(), mockk()),
            WebSocketEvent.OnMessageReceived(
                Message.Text(
                    Payload<Hello>(
                        10,
                        null,
                        "HELLO",
                        Hello(10, emptyArray()).toJsonTree()
                    ).toJson()
                )
            ),
            WebSocketEvent.OnMessageReceived(
                Message.Bytes(
                    Payload<Hello>(
                        2,
                        null,
                        "HELLO",
                        Hello(10, emptyArray()).toJsonTree()
                    ).toJson().toByteArray()
                )
            ),
            WebSocketEvent.OnConnectionClosing(ShutdownReason.GRACEFUL),
            WebSocketEvent.OnConnectionClosed(ShutdownReason.GRACEFUL)
        )
        impl = object : Implementation {
            override fun send(data: Payload<out GatewayCommand>): Boolean = buffer.add(data)

            override fun receive(): Stream<WebSocketEvent> = FlowableStream(innerFlowable)
        }
    }

    @ExperimentalStdlibApi
    @ExperimentalCoroutinesApi
    @ExperimentalTime
    @Test
    fun `gateway on start should produce 5 events with list interpreter`() {
        val events = gateway.run(impl.listKInterpreter).fold(ListK.applicative())
            .fix()

        events.size `should equal` 5
        events.map { it.opcode } `should contain same` arrayListOf(-1, 10, 2, -1, -1)
    }

    @Test
    @ExperimentalStdlibApi
    @ExperimentalCoroutinesApi
    @ExperimentalTime
    fun `gateway on start should produce 5 events with flowable interpreter`() {
        val events = gateway.run(impl.flowableInterpreter).fold(FlowableK.applicative())
            .fix()

        events.size(Long.monoid()) `should equal` 5
        events.map { it.opcode }.flowable.blockingIterable() `should contain same` arrayListOf(-1, 10, 2, -1, -1)
    }

    @ExperimentalCoroutinesApi
    @ExperimentalStdlibApi
    @ExperimentalTime
    @Test
    fun `gateway on start should produce 5 events with observable interpreter`() {
        val events = gateway.run(impl.observableInterpreter).fold(ObservableK.applicative())
            .fix()

        events.size(Long.monoid()) `should equal` 5
        events.map { it.opcode }.observable.blockingIterable() `should contain same` arrayListOf(-1, 10, 2, -1, -1)
    }
}
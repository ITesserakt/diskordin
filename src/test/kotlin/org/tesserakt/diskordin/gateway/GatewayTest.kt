package org.tesserakt.diskordin.gateway

import arrow.core.FunctionK
import arrow.core.extensions.monoid
import arrow.fx.rx2.FlowableK
import arrow.fx.rx2.ObservableK
import arrow.fx.rx2.extensions.flowablek.async.async
import arrow.fx.rx2.extensions.flowablek.foldable.foldable
import arrow.fx.rx2.extensions.observablek.async.async
import arrow.fx.rx2.extensions.observablek.foldable.foldable
import arrow.fx.typeclasses.Async
import arrow.typeclasses.Applicative
import arrow.typeclasses.Foldable
import arrow.typeclasses.Monoid
import com.tinder.scarlet.Message
import com.tinder.scarlet.Stream
import com.tinder.scarlet.websocket.ShutdownReason
import com.tinder.scarlet.websocket.WebSocketEvent
import io.mockk.mockk
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.amshove.kluent.`should be in`
import org.amshove.kluent.`should equal`
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.reactivestreams.Publisher
import org.tesserakt.diskordin.gateway.json.Payload
import org.tesserakt.diskordin.gateway.json.commands.Heartbeat
import org.tesserakt.diskordin.gateway.json.events.Hello
import org.tesserakt.diskordin.impl.core.client.GlobalGatewayLifecycle
import org.tesserakt.diskordin.impl.gateway.interpreter.flowableInterpreter
import org.tesserakt.diskordin.impl.gateway.interpreter.observableInterpreter
import org.tesserakt.diskordin.util.toJson
import org.tesserakt.diskordin.util.toJsonTree
import kotlin.time.ExperimentalTime

internal class GatewayTest {
    @ExperimentalCoroutinesApi
    @ExperimentalTime
    val gateway = Gateway(Dispatchers.Unconfined, GlobalGatewayLifecycle)

    @ExperimentalStdlibApi
    @ExperimentalCoroutinesApi
    @ExperimentalTime
    @ParameterizedTest
    @MethodSource(value = ["compilers"])
    fun <G> `gateway on start should produce 5 events`(
        compiler: FunctionK<ForGatewayAPIF, G>,
        A: Async<G>,
        FB: Foldable<G>
    ) = FB.run {
        val events = gateway.run(compiler, A)

        events.size(Long.monoid()) `should equal` 5
        A.run {
            events.map { it.opcode }.map { it `should be in` arrayListOf(-1, 10, 2, -1, -1) }
        }
        Unit
    }

    @Suppress("unused")
    @ParameterizedTest
    @MethodSource("compilers")
    fun <G> `sended data should appear in a buffer`(
        compiler: FunctionK<ForGatewayAPIF, G>,
        AP: Applicative<G>,
        FB: Foldable<G>
    ) = FB.run {
        sendPayload(Heartbeat(null), null).compile(compiler).fold(AP)
            .fold(object : Monoid<Boolean> {
                override fun empty(): Boolean = false

                override fun Boolean.combine(b: Boolean): Boolean = this or b
            }).shouldBeTrue()

        buffer.size `should equal` 1
        buffer.clear()
        Unit
    }

    companion object {
        val innerFlowable: Flowable<WebSocketEvent> = Flowable.fromArray(
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

        @JvmStatic
        private val buffer: MutableList<String> = mutableListOf()

        private val impl: Implementation = object : Implementation {
            override fun send(data: String): Boolean = buffer.add(data)

            override fun receive(): Stream<WebSocketEvent> = FlowableStream(innerFlowable)
        }

        @JvmStatic
        @Suppress("unused")
        fun compilers() = arrayOf(
            Arguments.of(impl.flowableInterpreter, FlowableK.async(), FlowableK.foldable()),
            Arguments.of(impl.observableInterpreter, ObservableK.async(), ObservableK.foldable())
        )
    }

    class FlowableStream<T>(
        private val flowable: Flowable<T>
    ) : Stream<T>, Publisher<T> by flowable {
        override fun start(observer: Stream.Observer<T>): Stream.Disposable {
            val disposable = flowable.subscribe(observer::onNext, observer::onError, observer::onComplete)
            return FlowableStreamDisposable(disposable)
        }

        class FlowableStreamDisposable(
            private val disposable: Disposable
        ) : Stream.Disposable {
            override fun dispose() = disposable.dispose()

            override fun isDisposed(): Boolean = disposable.isDisposed
        }
    }
}
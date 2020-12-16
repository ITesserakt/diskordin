package org.tesserakt.diskordin.gateway

import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import mu.KotlinLogging
import org.tesserakt.diskordin.gateway.json.Message
import kotlin.time.ExperimentalTime
import kotlin.time.minutes

interface GatewayConnectionImpl {
    @Send
    fun send(data: Message)

    @Receive
    fun receive(): com.tinder.scarlet.Stream<WebSocketEventWrap>
}

@OptIn(ExperimentalTime::class)
@ExperimentalCoroutinesApi
fun GatewayConnectionImpl.unwrap(shardId: Int, job: Job): GatewayConnection = object : GatewayConnection {
    private val logger = KotlinLogging.logger("[Shard #$shardId]")
    private val sendExceptionHandler = CoroutineExceptionHandler { _, t ->
        logger.error(t) { "Cannot send or receive data to discord" }
    }
    private val scope = CoroutineScope(Dispatchers.Unconfined + job + sendExceptionHandler)

    override suspend fun send(data: Message) = scope.launch {
        this@unwrap.send(data)
    }

    override fun receive() = callbackFlow<WebSocketEventWrap> {
        val dispatcher = this@callbackFlow
        val observer = object : com.tinder.scarlet.Stream.Observer<WebSocketEventWrap> {
            override fun onComplete() {
                dispatcher.close()
            }

            override fun onError(throwable: Throwable) {
                dispatcher.close(throwable)
            }

            override fun onNext(data: WebSocketEventWrap) {
                dispatcher.offer(data)
            }
        }
        val disposable = this@unwrap.receive().start(observer)
        awaitClose { disposable.dispose() }
    }.map { it.unwrap() }.shareIn(scope, SharingStarted.WhileSubscribed(replayExpiration = 5.minutes))
}
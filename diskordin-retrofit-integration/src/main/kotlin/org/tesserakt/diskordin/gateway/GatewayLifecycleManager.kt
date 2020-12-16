package org.tesserakt.diskordin.gateway

import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.LifecycleState
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.LifecycleRegistry
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ScarletGatewayLifecycleManager(
    private val registry: LifecycleRegistry,
    private val gatewayContext: CoroutineContext,
    private val shardId: Int,
    scarlet: () -> Scarlet
) : GatewayLifecycleManager, Lifecycle by registry {
    private val job = SupervisorJob(gatewayContext[Job])
    override lateinit var coroutineScope: CoroutineScope private set

    override suspend fun start() {
        coroutineScope = CoroutineScope(gatewayContext + job)
        registry.onNext(LifecycleState.Started)
    }

    override suspend fun restart() {
        job.cancelChildren()
        registry.onNext(LifecycleState.Stopped)
        registry.onNext(LifecycleState.Started)
    }

    override suspend fun stop(code: Short, message: String) {
        coroutineScope.cancel()
        registry.onNext(LifecycleState.Stopped)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val connection: GatewayConnection by lazy {
        scarlet().create<GatewayConnectionImpl>().unwrap(shardId, Job(job))
    }
}
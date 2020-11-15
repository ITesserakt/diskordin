package org.tesserakt.diskordin.gateway

import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.LifecycleState
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.LifecycleRegistry

class ScarletGatewayLifecycleManager(
    private val registry: LifecycleRegistry,
    scarlet: () -> Scarlet
) : GatewayLifecycleManager, Lifecycle by registry {
    override suspend fun start() {
        registry.onNext(LifecycleState.Started)
    }

    override suspend fun restart() {
        registry.onNext(LifecycleState.Stopped)
        registry.onNext(LifecycleState.Started)
    }

    override suspend fun stop(code: Short, message: String) {
        registry.onNext(LifecycleState.Stopped)
    }

    override val connection: GatewayConnection by lazy { scarlet().create<GatewayConnectionImpl>().unwrap() }
}
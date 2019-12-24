package org.tesserakt.diskordin.impl.core.client

import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.LifecycleState
import com.tinder.scarlet.lifecycle.LifecycleRegistry
import org.tesserakt.diskordin.core.client.IGatewayLifecycleManager

private val registry: LifecycleRegistry = LifecycleRegistry()

internal object GlobalGatewayLifecycle : IGatewayLifecycleManager, Lifecycle by registry {
    override fun start() = registry.onNext(LifecycleState.Started)
    override fun stop() = registry.onComplete()
    override fun restart() {
        registry.onNext(LifecycleState.Stopped)
        registry.onNext(LifecycleState.Started)
    }
}
package org.tesserakt.diskordin.impl.core.client

import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.LifecycleState
import com.tinder.scarlet.lifecycle.LifecycleRegistry

private val registry: LifecycleRegistry = LifecycleRegistry()

internal object GlobalGatewayLifecycle : Lifecycle by registry {
    fun start() = registry.onNext(LifecycleState.Started)
    fun stop() = registry.onComplete()

    fun restart() {
        registry.onNext(LifecycleState.Stopped)
        registry.onNext(LifecycleState.Started)
    }
}
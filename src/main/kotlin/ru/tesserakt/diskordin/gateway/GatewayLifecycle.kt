package ru.tesserakt.diskordin.gateway

import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.LifecycleState
import com.tinder.scarlet.lifecycle.LifecycleRegistry

internal class GatewayLifecycle(
    private val registry: LifecycleRegistry
) : Lifecycle by registry {
    fun start() = registry.onNext(LifecycleState.Started)

    fun restart() {
        registry.onNext(LifecycleState.Stopped)
    }

    fun stop() {
        registry.onComplete()
    }
}
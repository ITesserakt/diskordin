package org.tesserakt.diskordin.core.client

import com.tinder.scarlet.Lifecycle

interface IGatewayLifecycleManager : Lifecycle {
    fun start()
    fun stop()
    fun restart()
}
package org.tesserakt.diskordin.gateway.interceptor

import org.tesserakt.diskordin.gateway.Implementation
import org.tesserakt.diskordin.gateway.ShardController
import org.tesserakt.diskordin.gateway.json.commands.GatewayCommand
import org.tesserakt.diskordin.gateway.sendPayload
import kotlin.reflect.KClass

interface Interceptor<T : Interceptor.Context> {
    abstract class Context(
        internal val implementation: Implementation,
        val controller: ShardController,
        internal val sequenceId: () -> Int?
    )

    val selfContext: KClass<T>

    suspend fun intercept(context: T)

    suspend fun Context.sendPayload(data: GatewayCommand) =
        implementation.sendPayload(data, sequenceId())
}
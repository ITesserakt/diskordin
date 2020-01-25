package org.tesserakt.diskordin.gateway.interceptor

import org.tesserakt.diskordin.core.data.event.IEvent
import kotlin.reflect.KClass

abstract class EventInterceptor : Interceptor<EventInterceptor.Context> {
    data class Context(
        val event: IEvent
    ) : Interceptor.Context()

    override val selfContext: KClass<Context> = Context::class
}
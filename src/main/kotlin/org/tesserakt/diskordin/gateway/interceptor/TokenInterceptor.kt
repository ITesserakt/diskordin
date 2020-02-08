package org.tesserakt.diskordin.gateway.interceptor

import org.tesserakt.diskordin.gateway.Implementation
import org.tesserakt.diskordin.gateway.ShardController
import org.tesserakt.diskordin.gateway.json.IToken
import org.tesserakt.diskordin.gateway.sequenceId
import kotlin.reflect.KClass

abstract class TokenInterceptor : Interceptor<TokenInterceptor.Context> {
    class Context(
        impl: Implementation,
        val token: IToken,
        controller: ShardController
    ) : Interceptor.Context(impl, controller, ::sequenceId)

    override val selfContext: KClass<Context> = Context::class
}
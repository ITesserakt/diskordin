package org.tesserakt.diskordin.gateway.interceptor

import org.tesserakt.diskordin.gateway.Implementation
import org.tesserakt.diskordin.gateway.json.IToken
import kotlin.reflect.KClass

abstract class TokenInterceptor : Interceptor<TokenInterceptor.Context> {
    class Context(
        impl: Implementation,
        val token: IToken
    ) : Interceptor.Context(impl)

    override val selfContext: KClass<Context> = Context::class
}
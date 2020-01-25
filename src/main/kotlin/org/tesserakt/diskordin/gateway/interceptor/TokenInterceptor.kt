package org.tesserakt.diskordin.gateway.interceptor

import org.tesserakt.diskordin.gateway.json.IToken
import kotlin.reflect.KClass

abstract class TokenInterceptor : Interceptor<TokenInterceptor.Context> {
    data class Context(
        val token: IToken
    ) : Interceptor.Context()

    override val selfContext: KClass<Context> = Context::class
}
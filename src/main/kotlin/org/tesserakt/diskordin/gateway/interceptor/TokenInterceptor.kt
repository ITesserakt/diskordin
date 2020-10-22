package org.tesserakt.diskordin.gateway.interceptor

import org.tesserakt.diskordin.gateway.json.IToken
import org.tesserakt.diskordin.gateway.shard.Shard
import org.tesserakt.diskordin.gateway.shard.ShardController
import kotlin.reflect.KClass

abstract class TokenInterceptor : Interceptor<TokenInterceptor.Context> {
    class Context(
        val token: IToken,
        controller: ShardController,
        shard: Shard
    ) : Interceptor.Context(controller, shard)

    override val selfContext: KClass<Context> = Context::class
}
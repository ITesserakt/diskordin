package org.tesserakt.diskordin.gateway.interceptor

import org.tesserakt.diskordin.gateway.GatewayConnection
import org.tesserakt.diskordin.gateway.json.IToken
import org.tesserakt.diskordin.gateway.sequenceId
import org.tesserakt.diskordin.gateway.shard.ShardController
import kotlin.reflect.KClass

abstract class TokenInterceptor : Interceptor<TokenInterceptor.Context> {
    class Context(
        impl: GatewayConnection,
        val token: IToken,
        controller: ShardController,
        shardIndex: Int
    ) : Interceptor.Context(impl, controller, ::sequenceId, shardIndex)

    override val selfContext: KClass<Context> = Context::class
}
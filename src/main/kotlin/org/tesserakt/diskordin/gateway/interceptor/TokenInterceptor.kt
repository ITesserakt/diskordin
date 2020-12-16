package org.tesserakt.diskordin.gateway.interceptor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job() + exceptionHandler)
    final override val name: String = super.name
    final override suspend fun interceptWithJob(context: Context) = super.interceptWithJob(context)
}
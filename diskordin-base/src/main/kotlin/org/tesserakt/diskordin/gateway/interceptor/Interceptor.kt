package org.tesserakt.diskordin.gateway.interceptor

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import mu.KLogging
import org.tesserakt.diskordin.gateway.shard.Shard
import org.tesserakt.diskordin.gateway.shard.ShardController
import kotlin.reflect.KClass

interface Interceptor<C : Interceptor.Context> {
    companion object : KLogging()

    abstract class Context constructor(
        val controller: ShardController,
        val shard: Shard
    )

    val scope: CoroutineScope
    val exceptionHandler
        get() = CoroutineExceptionHandler { _, t ->
            logger.error(t) { "Unexpected fail on $name while processing event" }
        }
    private val Context.job get() = shard.lifecycle.coroutineScope.coroutineContext[Job] ?: Job()
    val name get() = "Interceptor#${hashCode()}"
    val selfContext: KClass<C>

    suspend fun interceptWithJob(context: C) = scope.launch(context.job) { intercept(context) }

    suspend fun intercept(context: C)
}
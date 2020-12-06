package org.tesserakt.diskordin.core.cache

import mu.KotlinLogging

internal fun interface CacheHandler<in T> {
    fun handle(builder: MemoryCacheSnapshot, data: T): MemoryCacheSnapshot
    val logger get() = KotlinLogging.logger { }
}

internal fun interface CacheDeleter<in T> : CacheHandler<T>

internal fun interface CacheUpdater<in T> : CacheHandler<T>

internal val NoopHandler = CacheHandler<Any?> { it, _ -> it }
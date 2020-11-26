package org.tesserakt.diskordin.core.cache

import mu.KotlinLogging

fun interface CacheHandler<in T> {
    fun handle(builder: CacheSnapshotBuilder, data: T)
    val enabled: Boolean get() = false
    val logger get() = KotlinLogging.logger { }
}

fun interface CacheDeleter<in T> : CacheHandler<T>

fun interface CacheUpdater<in T> : CacheHandler<T>

val NoopHandler = CacheHandler<Nothing?> { _, _ -> }
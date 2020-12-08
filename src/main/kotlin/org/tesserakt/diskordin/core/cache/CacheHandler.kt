package org.tesserakt.diskordin.core.cache

import mu.KotlinLogging
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IEntity

internal fun interface CacheHandler<in T> {
    fun handle(builder: CacheSnapshotBuilder, data: T)

    fun handleAndGet(builder: CacheSnapshotBuilder, data: T) = builder.apply { handle(this, data) }
    val logger get() = KotlinLogging.logger { }
}

internal fun interface CacheDeleter<in T : IEntity> : CacheHandler<T> {
    fun delete(builder: CacheSnapshotBuilder, id: Snowflake)

    override fun handle(builder: CacheSnapshotBuilder, data: T) = delete(builder, data.id)
}

internal fun interface CacheUpdater<in T> : CacheHandler<T>

internal val NoopHandler = CacheHandler<Any?> { _, _ -> }
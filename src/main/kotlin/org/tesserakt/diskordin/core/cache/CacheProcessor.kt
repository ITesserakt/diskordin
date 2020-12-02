package org.tesserakt.diskordin.core.cache

import arrow.core.extensions.map.functorFilter.filterIsInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import mu.KotlinLogging
import kotlin.reflect.KClass

internal class CacheProcessor(private val handlers: Map<KClass<*>, CacheHandler<*>>) {
    private val logger = KotlinLogging.logger { }
    private val _state: MutableStateFlow<CacheState> =
        MutableStateFlow(CacheState(MemoryCacheSnapshot.empty(), MemoryCacheSnapshot.empty()))

    val state = _state.asStateFlow()

    inline fun <reified T : Any> put(data: T) = put(data, T::class)

    fun <T : Any> put(data: T, clazz: KClass<T>) {
        val handler = handlers.filterIsInstance(CacheUpdater::class.java)[clazz] as CacheUpdater<T>
        val cache = handler.handle(state.value.current, data)

        _state.value.update(cache)
    }
}
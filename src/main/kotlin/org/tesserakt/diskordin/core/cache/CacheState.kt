package org.tesserakt.diskordin.core.cache

class CacheState(val current: MemoryCacheSnapshot, val previous: MemoryCacheSnapshot) {
    fun update(new: MemoryCacheSnapshot) =
        if (new == current) this
        else CacheState(new, current)

    fun modify(f: (MemoryCacheSnapshot) -> MemoryCacheSnapshot) = update(f(current))
}
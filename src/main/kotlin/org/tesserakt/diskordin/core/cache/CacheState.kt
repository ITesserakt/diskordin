package org.tesserakt.diskordin.core.cache

class CacheState(private val current: CacheSnapshot, private val previous: CacheSnapshot) {
    fun update(new: CacheSnapshot) =
        if (new == current) this
        else CacheState(new, current)
}
package org.tesserakt.diskordin.util

class NoopMap<K, V> : MutableMap<K, V> {
    override val size: Int = 0

    override fun containsKey(key: K): Boolean = false

    override fun containsValue(value: V): Boolean = false

    override fun get(key: K): V? = null

    override fun isEmpty(): Boolean = true

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> = mutableSetOf()
    override val keys: MutableSet<K> = mutableSetOf()
    override val values: MutableCollection<V> = mutableListOf()

    override fun clear() = Unit

    override fun put(key: K, value: V): V? = null

    override fun putAll(from: Map<out K, V>) = Unit

    override fun remove(key: K): V? = null
}
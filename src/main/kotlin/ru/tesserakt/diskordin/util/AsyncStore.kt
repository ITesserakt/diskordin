@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package ru.tesserakt.diskordin.util

import arrow.core.identity
import arrow.higherkind

@higherkind
data class AsyncStore<S, V>(val state: S, val render: suspend (S) -> V) : AsyncStoreOf<S, V>, AsyncStoreKindedJ<S, V> {
    fun <A> map(f: (V) -> A): AsyncStore<S, A> =
        AsyncStore(state) { state -> f(render(state)) }

    fun <A> coflatMap(f: (AsyncStore<S, V>) -> A): AsyncStore<S, A> =
        AsyncStore(state) { next: S -> f(AsyncStore(next, render)) }

    suspend fun extract(): V = render(state)

    fun duplicate(): AsyncStore<S, AsyncStore<S, V>> = coflatMap(::identity)

    suspend fun move(newState: S): AsyncStore<S, V> = duplicate().render(newState)

    companion object
}
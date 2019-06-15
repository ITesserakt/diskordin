package ru.tesserakt.diskordin.core.entity.query

import kotlin.reflect.full.createInstance

typealias Query = List<Pair<String, *>>

interface IQuery {
    fun create(): Query
}

inline fun <reified Q : IQuery> (Q.() -> Unit).build() =
    Q::class.createInstance().apply(this).create()

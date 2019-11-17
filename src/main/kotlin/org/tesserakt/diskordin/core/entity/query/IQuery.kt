package org.tesserakt.diskordin.core.entity.query

import kotlin.reflect.full.createInstance

typealias Query = Map<String, String>

interface IQuery {
    fun create(): Query
}

inline fun <reified Q : IQuery> (Q.() -> Unit).query() =
    Q::class.createInstance().apply(this).create()

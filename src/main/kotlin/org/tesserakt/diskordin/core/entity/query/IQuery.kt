package org.tesserakt.diskordin.core.entity.query

typealias Query = Map<String, String>

interface IQuery {
    fun create(): Query
}

inline fun <reified Q : IQuery> (Q.() -> Unit).query(ctor: () -> Q) =
    ctor().apply(this).create()

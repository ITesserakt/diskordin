package ru.tesserakt.diskordin.core.entity.query

class PruneQuery : IQuery {
    lateinit var days: Unit

    override fun create(): Query = mapOf(
        "days" to days
    ).toList()
}

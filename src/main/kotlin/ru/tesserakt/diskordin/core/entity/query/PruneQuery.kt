package ru.tesserakt.diskordin.core.entity.query

class PruneQuery : IQuery {
    var days: Int = 0

    override fun create(): Query = mapOf(
        "days" to days.toString()
    )
}

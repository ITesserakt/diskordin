package ru.tesserakt.diskordin.core.entity.query

class BanQuery : IQuery {
    var reason: String? = null
    var deleteMessageDays: Int? = null

    override fun create(): List<Pair<String, *>> = mapOf(
        "reason" to reason,
        "delete_message_days" to deleteMessageDays
    ).toList()
}

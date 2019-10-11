package ru.tesserakt.diskordin.core.entity.query

class BanQuery : IQuery {
    var deleteMessageDays: Int? = null

    override fun create() = mapOf(
        "delete_message_days" to deleteMessageDays.toString()
    )
}

package ru.tesserakt.diskordin.core.data.json.request


data class PositionEditRequest(
    val id: Long,
    val position: Int
) : JsonRequest()

package ru.tesserakt.diskordin.core.data.json.request


data class GroupRecipientAddRequest(
    val accessToken: String,
    val nick: String
) : JsonRequest()

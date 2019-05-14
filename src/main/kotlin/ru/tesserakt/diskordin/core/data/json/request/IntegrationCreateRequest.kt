package ru.tesserakt.diskordin.core.data.json.request


data class IntegrationCreateRequest(
    val type: String,
    val id: Long
) : JsonRequest()

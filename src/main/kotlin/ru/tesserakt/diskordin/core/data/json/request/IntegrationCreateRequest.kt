package ru.tesserakt.diskordin.core.data.json.request

import ru.tesserakt.diskordin.core.data.Snowflake


data class IntegrationCreateRequest(
    val type: String,
    val id: Snowflake
) : JsonRequest()

package ru.tesserakt.diskordin.core.data.json.request

import ru.tesserakt.diskordin.core.data.Snowflake


data class DMCreateRequest(
    val recipient_id: Snowflake
) : JsonRequest()

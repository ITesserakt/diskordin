package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.request.IntegrationCreateRequest

class IntegrationCreateBuilder : BuilderBase<IntegrationCreateRequest>() {
    lateinit var type: String
    lateinit var id: Snowflake

    override fun create(): IntegrationCreateRequest = IntegrationCreateRequest(
        type,
        id
    )
}

package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.IntegrationCreateRequest

class IntegrationCreateBuilder : BuilderBase<IntegrationCreateRequest>() {
    lateinit var type: String
    lateinit var id: Snowflake

    override fun create(): IntegrationCreateRequest = IntegrationCreateRequest(
        type,
        id
    )
}

package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.IntegrationCreateRequest

@RequestBuilder
class IntegrationCreateBuilder(val id: Snowflake, val type: String) : BuilderBase<IntegrationCreateRequest>() {
    override fun create(): IntegrationCreateRequest = IntegrationCreateRequest(
        type,
        id
    )
}

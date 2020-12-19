package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.DMCreateRequest

@RequestBuilder
class DMCreateBuilder(val recipientId: Snowflake) : BuilderBase<DMCreateRequest>() {
    override fun create(): DMCreateRequest = DMCreateRequest(recipientId)
}

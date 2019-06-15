package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.request.DMCreateRequest
import kotlin.properties.Delegates

class DMCreateBuilder : BuilderBase<DMCreateRequest>() {
    var recipientId: Snowflake by Delegates.notNull()

    override fun create(): DMCreateRequest = DMCreateRequest(
        recipientId.asLong()
    )
}

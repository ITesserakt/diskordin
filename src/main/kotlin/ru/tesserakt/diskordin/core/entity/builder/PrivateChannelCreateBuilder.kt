package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.request.DMCreateRequest

class PrivateChannelCreateBuilder : IBuilder<DMCreateRequest> {

    lateinit var recipientId: Snowflake


    override fun create(): DMCreateRequest = DMCreateRequest(
        recipientId.asLong()
    )
}
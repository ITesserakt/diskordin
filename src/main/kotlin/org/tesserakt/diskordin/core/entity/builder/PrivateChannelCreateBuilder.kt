package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.DMCreateRequest

class PrivateChannelCreateBuilder : BuilderBase<DMCreateRequest>() {

    lateinit var recipientId: Snowflake


    override fun create(): DMCreateRequest = DMCreateRequest(
        recipientId
    )
}
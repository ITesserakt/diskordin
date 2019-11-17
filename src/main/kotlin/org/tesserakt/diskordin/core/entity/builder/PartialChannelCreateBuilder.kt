package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.json.request.PartialChannelCreateRequest
import org.tesserakt.diskordin.core.entity.IChannel

class PartialChannelCreateBuilder : BuilderBase<PartialChannelCreateRequest>() {
    lateinit var name: String
    lateinit var type: IChannel.Type

    override fun create(): PartialChannelCreateRequest = PartialChannelCreateRequest(
        name,
        type.ordinal
    )
}

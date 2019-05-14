package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.json.request.PartialChannelCreateRequest
import ru.tesserakt.diskordin.core.entity.IChannel

class PartialChannelCreateBuilder : IBuilder<PartialChannelCreateRequest> {
    lateinit var name: String
    lateinit var type: IChannel.Type

    override fun create(): PartialChannelCreateRequest = PartialChannelCreateRequest(
        name,
        type.ordinal
    )
}

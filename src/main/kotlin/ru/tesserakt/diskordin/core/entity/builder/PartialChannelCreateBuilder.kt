package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.entity.IChannel

class PartialChannelCreateBuilder : BuilderBase<PartialChannelCreateRequest>() {
    lateinit var name: String
    lateinit var type: IChannel.Type

    override fun create(): PartialChannelCreateRequest = PartialChannelCreateRequest(
        name,
        type.ordinal
    )
}

package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.json.request.PartialChannelCreateRequest
import org.tesserakt.diskordin.core.entity.IChannel

@RequestBuilder
class PartialChannelCreateBuilder(val name: String, val type: IChannel.Type) :
    BuilderBase<PartialChannelCreateRequest>() {
    override fun create(): PartialChannelCreateRequest = PartialChannelCreateRequest(
        name,
        type.ordinal
    )
}

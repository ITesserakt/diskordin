package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.json.request.MessageEditRequest

class MessageEditBuilder : BuilderBase<MessageEditRequest>() {
    var content: String? = null
    var embed: (EmbedCreateBuilder.() -> Unit)? = null

    override fun create(): MessageEditRequest = MessageEditRequest(
        content,
        embed?.let { EmbedCreateBuilder().apply(it).create() }
    )
}
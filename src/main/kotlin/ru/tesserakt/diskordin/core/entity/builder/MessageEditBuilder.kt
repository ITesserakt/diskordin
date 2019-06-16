package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.json.request.MessageEditRequest

class MessageEditBuilder : BuilderBase<MessageEditRequest>() {
    val content: String? = null
    val embed: (EmbedCreateBuilder.() -> Unit)? = null

    override fun create(): MessageEditRequest = MessageEditRequest(
        content,
        embed?.let { EmbedCreateBuilder().apply(it).create() }
    )
}
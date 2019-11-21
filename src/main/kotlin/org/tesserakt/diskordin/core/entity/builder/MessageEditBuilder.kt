package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.json.request.MessageEditRequest

@RequestBuilder
@Suppress("NOTHING_TO_INLINE", "unused")
class MessageEditBuilder : BuilderBase<MessageEditRequest>() {
    private var content: String? = null
    private var embed: EmbedCreateBuilder? = null

    override fun create(): MessageEditRequest = MessageEditRequest(
        content,
        embed?.create()
    )

    operator fun String.unaryPlus() {
        content = this
    }

    operator fun EmbedCreateBuilder.unaryPlus() {
        this@MessageEditBuilder.embed = this
    }

    inline fun MessageEditBuilder.content(value: String) = value
    inline fun MessageEditBuilder.embed(builder: EmbedCreateBuilder.() -> Unit) = EmbedCreateBuilder().apply(builder)
}
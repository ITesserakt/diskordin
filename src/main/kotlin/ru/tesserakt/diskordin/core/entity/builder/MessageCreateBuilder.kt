@file:Suppress("MemberVisibilityCanBePrivate")

package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.request.MessageCreateRequest

class MessageCreateBuilder : BuilderBase<MessageCreateRequest>() {
    var content: String = ""
    var nonce: Snowflake? = null
    var tts: Boolean? = null
    var embed: (EmbedCreateBuilder.() -> Unit)? = null

    override fun create(): MessageCreateRequest {
        require(content.isNotEmpty() || embed != null) { "You must send at least one of content or embed" }
        return MessageCreateRequest(
            content,
            nonce?.asLong(),
            tts,
            embed?.build()
        )
    }
}
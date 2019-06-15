package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.json.request.MessageCreateRequest

class MessageCreateBuilder : BuilderBase<MessageCreateRequest>() {
    var content: String = ""
    var nonce: Snowflake? = null
    var tts: Boolean? = null
    var file: File? = null
    var embed: (EmbedCreateBuilder.() -> Unit)? = null

    override fun create(): MessageCreateRequest = MessageCreateRequest(
        content,
        nonce?.asLong(),
        tts,
        file?.inputStream()?.asInput(),
        embed?.build()
    )
}
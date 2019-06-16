package ru.tesserakt.diskordin.core.entity.builder

import kotlinx.io.streams.asInput
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.request.MessageCreateRequest
import java.io.File

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
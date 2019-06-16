package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.request.EmojiCreateRequest
import java.io.File

class EmojiCreateBuilder : BuilderBase<EmojiCreateRequest>() {
    lateinit var name: String
    lateinit var image: File
    lateinit var roles: Array<Snowflake>

    override fun create(): EmojiCreateRequest = EmojiCreateRequest(
        name,
        image,
        roles
    )
}

package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.json.request.EmojiEditRequest

class EmojiEditBuilder : BuilderBase<EmojiEditRequest>() {
    lateinit var name: String
    lateinit var roles: Array<Snowflake>

    override fun create(): EmojiEditRequest = EmojiEditRequest(
        name,
        roles
    )
}

package ru.tesserakt.diskordin.core.entity.builder

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

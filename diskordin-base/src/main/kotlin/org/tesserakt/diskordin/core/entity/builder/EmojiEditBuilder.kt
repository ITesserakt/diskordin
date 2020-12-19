package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.EmojiEditRequest

@RequestBuilder
class EmojiEditBuilder : BuilderBase<EmojiEditRequest>() {
    lateinit var name: String
    lateinit var roles: Array<Snowflake>

    override fun create(): EmojiEditRequest = EmojiEditRequest(
        name,
        roles
    )
}

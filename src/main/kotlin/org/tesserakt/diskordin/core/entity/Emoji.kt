package org.tesserakt.diskordin.core.entity

import arrow.core.ForId
import arrow.core.ListK
import arrow.fx.IO
import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.EmojiResponse
import org.tesserakt.diskordin.core.entity.builder.EmojiEditBuilder
import org.tesserakt.diskordin.impl.core.entity.CustomEmoji
import org.tesserakt.diskordin.impl.core.entity.Emoji

interface IEmoji : INamed {
    companion object {
        @Suppress("UNCHECKED_CAST")
        internal fun <E : IEmoji> typed(raw: EmojiResponse<E>, guildId: Snowflake? = null) = when {
            raw.id != null && guildId != null -> CustomEmoji(raw as EmojiResponse<ICustomEmoji>, guildId)
            else -> Emoji(raw)
        } as E
    }
}

interface ICustomEmoji : IEmoji, IDeletable, IMentioned, IGuildObject, IEditable<ICustomEmoji, EmojiEditBuilder> {
    val roles: IO<ListK<IRole>>
    val creator: IdentifiedF<ForId, IUser>
    val requireColons: Boolean
    val isManaged: Boolean
    val isAnimated: Boolean

    fun edit(name: String, roles: Array<Snowflake>) = edit {
        this.name = name
        this.roles = roles
    }
}
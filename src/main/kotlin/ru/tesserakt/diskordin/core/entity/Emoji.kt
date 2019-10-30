package ru.tesserakt.diskordin.core.entity

import kotlinx.coroutines.flow.Flow
import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.response.EmojiResponse
import ru.tesserakt.diskordin.core.entity.builder.EmojiEditBuilder
import ru.tesserakt.diskordin.impl.core.entity.CustomEmoji
import ru.tesserakt.diskordin.impl.core.entity.Emoji

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
    val roles: Flow<IRole>
    val creator: Identified<IUser>
    val requireColons: Boolean
    val isManaged: Boolean
    val isAnimated: Boolean
}
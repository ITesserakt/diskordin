package ru.tesserakt.diskordin.core.entity

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.response.EmojiResponse
import ru.tesserakt.diskordin.core.entity.builder.EmojiEditBuilder
import ru.tesserakt.diskordin.impl.core.entity.CustomEmoji
import ru.tesserakt.diskordin.impl.core.entity.Emoji
import ru.tesserakt.diskordin.util.Identified

interface IEmoji : INamed {
    companion object {
        inline fun <reified E : IEmoji> typed(raw: EmojiResponse, guildId: Snowflake? = null) = when {
            raw.id != null && guildId != null -> CustomEmoji(raw, guildId)
            else -> Emoji(raw)
        } as E
    }
}

interface ICustomEmoji : IEmoji, IDeletable, IMentioned, IGuildObject, IEditable<ICustomEmoji, EmojiEditBuilder> {
    @ExperimentalCoroutinesApi
    val roles: Flow<IRole>
    val creator: Identified<IUser>
    val requireColons: Boolean
    val isManaged: Boolean
    val isAnimated: Boolean
}
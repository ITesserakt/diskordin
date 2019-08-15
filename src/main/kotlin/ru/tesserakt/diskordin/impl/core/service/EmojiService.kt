package ru.tesserakt.diskordin.impl.core.service

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.ICustomEmoji
import ru.tesserakt.diskordin.core.entity.IEmoji
import ru.tesserakt.diskordin.core.entity.builder.EmojiCreateBuilder
import ru.tesserakt.diskordin.core.entity.builder.EmojiEditBuilder
import ru.tesserakt.diskordin.core.entity.builder.build
import ru.tesserakt.diskordin.rest.resource.EmojiResource

internal object EmojiService {
    //private val emojiCache = genericCache<ICustomEmoji>()

    suspend fun getEmojis(guildId: Snowflake) =
        EmojiResource.General.getEmojis(guildId.asLong())
            .map { IEmoji.typed<ICustomEmoji>(it, guildId) }

    suspend fun getEmoji(guildId: Snowflake, emojiId: Snowflake) =
        EmojiResource.General.getEmoji(guildId.asLong(), emojiId.asLong())
            .let { IEmoji.typed<ICustomEmoji>(it, guildId) }

    suspend fun createEmoji(guildId: Snowflake, builder: EmojiCreateBuilder.() -> Unit) =
        EmojiResource.General.createEmoji(guildId.asLong(), builder.build())
            .let { IEmoji.typed<ICustomEmoji>(it, guildId) }

    suspend fun editEmoji(guildId: Snowflake, emojiId: Snowflake, builder: EmojiEditBuilder.() -> Unit) =
        EmojiResource.General
            .modifyEmoji(guildId.asLong(), emojiId.asLong(), builder.build())
            .let { IEmoji.typed<ICustomEmoji>(it) }

    suspend fun deleteEmoji(guildId: Snowflake, emojiId: Snowflake) =
        EmojiResource.General.deleteEmoji(guildId.asLong(), emojiId.asLong())
}
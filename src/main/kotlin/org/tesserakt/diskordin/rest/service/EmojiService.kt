@file:Suppress("unused")

package org.tesserakt.diskordin.rest.service

import arrow.core.ListK
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.EmojiCreateRequest
import org.tesserakt.diskordin.core.data.json.request.EmojiEditRequest
import org.tesserakt.diskordin.core.data.json.response.EmojiResponse
import org.tesserakt.diskordin.core.entity.ICustomEmoji

interface EmojiService {
    suspend fun getGuildEmojis(id: Snowflake): ListK<EmojiResponse<ICustomEmoji>>

    suspend fun getGuildEmoji(
        guildId: Snowflake,
        emojiId: Snowflake
    ): EmojiResponse<ICustomEmoji>

    suspend fun createGuildEmoji(
        id: Snowflake,
        request: EmojiCreateRequest
    ): EmojiResponse<ICustomEmoji>

    suspend fun editGuildEmoji(
        guildId: Snowflake,
        emojiId: Snowflake,
        request: EmojiEditRequest
    ): EmojiResponse<ICustomEmoji>

    suspend fun deleteGuildEmoji(
        guildId: Snowflake,
        emojiId: Snowflake
    )
}
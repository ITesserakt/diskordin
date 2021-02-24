package org.tesserakt.diskordin.rest.service


import io.ktor.client.*
import io.ktor.client.request.*
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.EmojiCreateRequest
import org.tesserakt.diskordin.core.data.json.request.EmojiEditRequest
import org.tesserakt.diskordin.core.data.json.response.EmojiResponse
import org.tesserakt.diskordin.core.entity.ICustomEmoji

class EmojiServiceImpl(private val ktor: HttpClient, private val discordApiUrl: String) : EmojiService {
    override suspend fun getGuildEmojis(id: Snowflake): List<EmojiResponse<ICustomEmoji>> =
        ktor.get("$discordApiUrl/api/v6/guilds/$id/emojis")

    override suspend fun getGuildEmoji(guildId: Snowflake, emojiId: Snowflake): EmojiResponse<ICustomEmoji> =
        ktor.get("$discordApiUrl/api/v6/guilds/$guildId/emojis/$emojiId")

    override suspend fun createGuildEmoji(id: Snowflake, request: EmojiCreateRequest): EmojiResponse<ICustomEmoji> =
        ktor.post("$discordApiUrl/api/v6/guilds/$id/emojis/") {
            body = request
        }

    override suspend fun editGuildEmoji(
        guildId: Snowflake,
        emojiId: Snowflake,
        request: EmojiEditRequest
    ): EmojiResponse<ICustomEmoji> = ktor.patch("$discordApiUrl/api/v6/guilds/$guildId/emojis/$emojiId") {
        body = request
    }

    override suspend fun deleteGuildEmoji(guildId: Snowflake, emojiId: Snowflake): Unit =
        ktor.delete("$discordApiUrl/api/v6/guilds/$guildId/emojis/$emojiId")
}
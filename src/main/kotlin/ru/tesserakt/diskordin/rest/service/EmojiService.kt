@file:Suppress("unused")

package ru.tesserakt.diskordin.rest.service

import retrofit2.http.*
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.request.EmojiCreateRequest
import ru.tesserakt.diskordin.core.data.json.request.EmojiEditRequest
import ru.tesserakt.diskordin.core.data.json.response.EmojiResponse
import ru.tesserakt.diskordin.core.entity.ICustomEmoji

interface EmojiService {
    @GET("/api/v6/guilds/{id}/emojis")
    suspend fun getGuildEmojis(@Path("id") id: Snowflake): Array<EmojiResponse<ICustomEmoji>>

    @GET("/api/v6/guilds/{guildId}/emojis/{emojiId}")
    suspend fun getGuildEmoji(
        @Path("guildId") guildId: Snowflake,
        @Path("emojiId") emojiId: Snowflake
    ): EmojiResponse<ICustomEmoji>

    @POST("/api/v6/guilds/{id}/emojis/")
    suspend fun createGuildEmoji(
        @Path("id") id: Snowflake,
        @Body request: EmojiCreateRequest
    ): EmojiResponse<ICustomEmoji>

    @PATCH("/api/v6/guilds/{guildId}/emojis/{emojiId}")
    suspend fun editGuildEmoji(
        @Path("guildId") guildId: Snowflake,
        @Path("emojiId") emojiId: Snowflake,
        @Body request: EmojiEditRequest
    ): EmojiResponse<ICustomEmoji>

    @DELETE("/api/v6/guilds/{guildId}/emojis/{emojiId}")
    suspend fun deleteGuildEmoji(
        @Path("guildId") guildId: Snowflake,
        @Path("emojiId") emojiId: Snowflake
    )
}
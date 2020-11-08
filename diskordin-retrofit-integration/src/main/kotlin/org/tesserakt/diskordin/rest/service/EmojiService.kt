package org.tesserakt.diskordin.rest.service

import arrow.core.ListK
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.EmojiCreateRequest
import org.tesserakt.diskordin.core.data.json.request.EmojiEditRequest
import org.tesserakt.diskordin.core.data.json.response.EmojiResponse
import org.tesserakt.diskordin.core.entity.ICustomEmoji
import retrofit2.http.*

interface EmojiServiceImpl : EmojiService {
    @GET("/api/v6/guilds/{id}/emojis")
    override suspend fun getGuildEmojis(@Path("id") id: Snowflake): ListK<EmojiResponse<ICustomEmoji>>

    @GET("/api/v6/guilds/{guildId}/emojis/{emojiId}")
    override suspend fun getGuildEmoji(
        @Path("guildId") guildId: Snowflake,
        @Path("emojiId") emojiId: Snowflake
    ): EmojiResponse<ICustomEmoji>

    @POST("/api/v6/guilds/{id}/emojis/")
    override suspend fun createGuildEmoji(
        @Path("id") id: Snowflake,
        @Body request: EmojiCreateRequest
    ): EmojiResponse<ICustomEmoji>

    @PATCH("/api/v6/guilds/{guildId}/emojis/{emojiId}")
    override suspend fun editGuildEmoji(
        @Path("guildId") guildId: Snowflake,
        @Path("emojiId") emojiId: Snowflake,
        @Body request: EmojiEditRequest
    ): EmojiResponse<ICustomEmoji>

    @DELETE("/api/v6/guilds/{guildId}/emojis/{emojiId}")
    override suspend fun deleteGuildEmoji(
        @Path("guildId") guildId: Snowflake,
        @Path("emojiId") emojiId: Snowflake
    )
}
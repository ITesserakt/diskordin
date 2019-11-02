@file:Suppress("unused")

package ru.tesserakt.diskordin.rest.service

import arrow.core.Id
import arrow.core.ListK
import arrow.integrations.retrofit.adapter.CallK
import retrofit2.http.*
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.request.EmojiCreateRequest
import ru.tesserakt.diskordin.core.data.json.request.EmojiEditRequest
import ru.tesserakt.diskordin.core.data.json.response.EmojiResponse
import ru.tesserakt.diskordin.core.entity.ICustomEmoji

interface EmojiService {
    @GET("/api/v6/guilds/{id}/emojis")
    fun getGuildEmojis(@Path("id") id: Snowflake): CallK<ListK<EmojiResponse<ICustomEmoji>>>

    @GET("/api/v6/guilds/{guildId}/emojis/{emojiId}")
    fun getGuildEmoji(
        @Path("guildId") guildId: Snowflake,
        @Path("emojiId") emojiId: Snowflake
    ): CallK<Id<EmojiResponse<ICustomEmoji>>>

    @POST("/api/v6/guilds/{id}/emojis/")
    fun createGuildEmoji(
        @Path("id") id: Snowflake,
        @Body request: EmojiCreateRequest
    ): CallK<Id<EmojiResponse<ICustomEmoji>>>

    @PATCH("/api/v6/guilds/{guildId}/emojis/{emojiId}")
    fun editGuildEmoji(
        @Path("guildId") guildId: Snowflake,
        @Path("emojiId") emojiId: Snowflake,
        @Body request: EmojiEditRequest
    ): CallK<Id<EmojiResponse<ICustomEmoji>>>

    @DELETE("/api/v6/guilds/{guildId}/emojis/{emojiId}")
    fun deleteGuildEmoji(
        @Path("guildId") guildId: Snowflake,
        @Path("emojiId") emojiId: Snowflake
    ): CallK<Unit>
}
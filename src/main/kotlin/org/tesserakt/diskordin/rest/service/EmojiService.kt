@file:Suppress("unused")

package org.tesserakt.diskordin.rest.service

import arrow.core.Id
import arrow.core.ListK
import arrow.integrations.retrofit.adapter.CallK
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.EmojiCreateRequest
import org.tesserakt.diskordin.core.data.json.request.EmojiEditRequest
import org.tesserakt.diskordin.core.data.json.response.EmojiResponse
import org.tesserakt.diskordin.core.entity.ICustomEmoji
import retrofit2.http.*

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
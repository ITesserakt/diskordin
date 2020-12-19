package org.tesserakt.diskordin.core.data.json.response

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IMessage
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.impl.core.entity.Message


data class MessageResponse(
    val id: Snowflake,
    val channel_id: Snowflake,
    val guild_id: Snowflake? = null,
    val author: UserResponse<IUser>?,
    val member: GuildMemberResponse? = null,
    val content: String,
    val timestamp: String,
    val edited_timestamp: String?,
    val tts: Boolean,
    val mention_everyone: Boolean,
    val mentions: List<MessageUserResponse>,
    val mention_roles: List<Long>? = null,
    val attachments: List<AttachmentResponse>?,
    val embeds: List<EmbedResponse>,
    val reactions: List<ReactionResponse>? = null,
    val nonce: Long? = null,
    val pinned: Boolean,
    val webhook_id: Snowflake? = null,
    val type: Int
) : DiscordResponse<IMessage, UnwrapContext.EmptyContext>() {
    override fun unwrap(ctx: UnwrapContext.EmptyContext): IMessage = Message(this)
}
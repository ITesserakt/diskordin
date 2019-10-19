package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.IMessage
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.impl.core.entity.Message


data class MessageResponse(
    val id: Snowflake,
    val channel_id: Snowflake,
    val guild_id: Snowflake? = null,
    val author: UserResponse<IUser>,
    val member: GuildMemberResponse? = null,
    val content: String,
    val timestamp: String,
    val edited_timestamp: String?,
    val tts: Boolean,
    val mention_everyone: Boolean,
    val mentions: Array<MessageUserResponse>,
    val mention_roles: Array<Long>? = null,
    val attachments: Array<AttachmentResponse>,
    val embeds: Array<EmbedResponse>,
    val reactions: Array<ReactionResponse>? = null,
    val nonce: Long? = null,
    val pinned: Boolean,
    val webhook_id: Snowflake? = null,
    val type: Int
) : DiscordResponse<IMessage>() {
    override fun unwrap(vararg params: Any): IMessage = Message(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MessageResponse

        if (id != other.id) return false
        if (channel_id != other.channel_id) return false
        if (guild_id != other.guild_id) return false
        if (author != other.author) return false
        if (member != other.member) return false
        if (content != other.content) return false
        if (timestamp != other.timestamp) return false
        if (edited_timestamp != other.edited_timestamp) return false
        if (tts != other.tts) return false
        if (mention_everyone != other.mention_everyone) return false
        if (!mentions.contentEquals(other.mentions)) return false
        if (mention_roles != null) {
            if (other.mention_roles == null) return false
            if (!mention_roles.contentEquals(other.mention_roles)) return false
        } else if (other.mention_roles != null) return false
        if (!attachments.contentEquals(other.attachments)) return false
        if (!embeds.contentEquals(other.embeds)) return false
        if (reactions != null) {
            if (other.reactions == null) return false
            if (!reactions.contentEquals(other.reactions)) return false
        } else if (other.reactions != null) return false
        if (nonce != other.nonce) return false
        if (pinned != other.pinned) return false
        if (webhook_id != other.webhook_id) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + channel_id.hashCode()
        result = 31 * result + (guild_id?.hashCode() ?: 0)
        result = 31 * result + author.hashCode()
        result = 31 * result + member.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + (edited_timestamp?.hashCode() ?: 0)
        result = 31 * result + tts.hashCode()
        result = 31 * result + mention_everyone.hashCode()
        result = 31 * result + mentions.contentHashCode()
        result = 31 * result + (mention_roles?.contentHashCode() ?: 0)
        result = 31 * result + attachments.contentHashCode()
        result = 31 * result + embeds.contentHashCode()
        result = 31 * result + (reactions?.contentHashCode() ?: 0)
        result = 31 * result + (nonce?.hashCode() ?: 0)
        result = 31 * result + pinned.hashCode()
        result = 31 * result + (webhook_id?.hashCode() ?: 0)
        result = 31 * result + type
        return result
    }
}
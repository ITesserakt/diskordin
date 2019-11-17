package org.tesserakt.diskordin.core.data.json.response

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.`object`.IVoiceState
import org.tesserakt.diskordin.impl.core.entity.`object`.VoiceState

data class VoiceStateResponse(
    val guildId: Snowflake? = null,
    val channelId: Snowflake?,
    val userId: Snowflake,
    val member: GuildMemberResponse? = null,
    val sessionId: String,
    val deaf: Boolean,
    val mute: Boolean,
    val selfDeaf: Boolean,
    val selfMute: Boolean,
    val selfStream: Boolean? = null,
    val suppress: Boolean
) : DiscordResponse<IVoiceState, UnwrapContext.EmptyContext>() {
    override fun unwrap(ctx: UnwrapContext.EmptyContext): IVoiceState = VoiceState(this)
}

package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.`object`.IVoiceState
import ru.tesserakt.diskordin.impl.core.entity.`object`.VoiceState

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

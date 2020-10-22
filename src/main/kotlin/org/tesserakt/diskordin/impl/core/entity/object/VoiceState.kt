package org.tesserakt.diskordin.impl.core.entity.`object`

import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.VoiceStateResponse
import org.tesserakt.diskordin.core.entity.IChannel
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.`object`.IVoiceState
import org.tesserakt.diskordin.core.entity.client

internal class VoiceState(raw: VoiceStateResponse) : IVoiceState {
    override val channel = raw.channelId?.identify<IChannel> { client.getChannel(it) }
    override val user = raw.userId.identify<IUser> { client.getUser(it) }
    override val guild = raw.guildId?.identify<IGuild> { client.getGuild(it) }
    override val sessionId: String = raw.sessionId
    override val isDeafen: Boolean = raw.deaf
    override val isMuted: Boolean = raw.mute
    override val isSelfDeafen: Boolean = raw.selfDeaf
    override val isSelfMuted: Boolean = raw.selfMute
    override val isStreaming: Boolean = raw.selfStream ?: false
    override val isSuppressed: Boolean = raw.suppress
    override fun toString(): String {
        return StringBuilder("VoiceState(")
            .appendLine("channel=$channel, ")
            .appendLine("user=$user, ")
            .appendLine("guild=$guild, ")
            .appendLine("sessionId='$sessionId', ")
            .appendLine("isDeafen=$isDeafen, ")
            .appendLine("isMuted=$isMuted, ")
            .appendLine("isSelfDeafen=$isSelfDeafen, ")
            .appendLine("isSelfMuted=$isSelfMuted, ")
            .appendLine("isStreaming=$isStreaming, ")
            .appendLine("isSuppressed=$isSuppressed")
            .appendLine(")")
            .toString()
    }
}

package org.tesserakt.diskordin.impl.core.entity.`object`

import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.VoiceStateResponse
import org.tesserakt.diskordin.core.entity.`object`.IVoiceState
import org.tesserakt.diskordin.core.entity.client

internal class VoiceState(raw: VoiceStateResponse) : IVoiceState {
    override val channel = raw.channelId?.identify { client.getChannel(it) }
    override val user = raw.userId identify { client.getUser(it) }
    override val guild = raw.guildId?.identify { client.getGuild(it) }
    override val sessionId: String = raw.sessionId
    override val isDeafen: Boolean = raw.deaf
    override val isMuted: Boolean = raw.mute
    override val isSelfDeafen: Boolean = raw.selfDeaf
    override val isSelfMuted: Boolean = raw.selfMute
    override val isStreaming: Boolean = raw.selfStream ?: false
    override val isSuppressed: Boolean = raw.suppress

    override fun toString(): String {
        return "VoiceState(channel=$channel, user=$user, guild=$guild, sessionId='$sessionId', isDeafen=$isDeafen, isMuted=$isMuted, isSelfDeafen=$isSelfDeafen, isSelfMuted=$isSelfMuted, isStreaming=$isStreaming, isSuppressed=$isSuppressed)"
    }
}

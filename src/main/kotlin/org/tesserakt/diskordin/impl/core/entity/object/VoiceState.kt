package org.tesserakt.diskordin.impl.core.entity.`object`

import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.VoiceStateResponse
import org.tesserakt.diskordin.core.entity.`object`.IVoiceState
import org.tesserakt.diskordin.core.entity.client

class VoiceState(raw: VoiceStateResponse) : IVoiceState {
    override val channel = raw.channelId?.identify { client.getChannel(it).bind() }
    override val user = raw.userId identify { client.getUser(it).bind() }
    override val guild = raw.guildId?.identify { client.getGuild(it).bind() }
    override val sessionId: String = raw.sessionId
    override val isDeafen: Boolean = raw.deaf
    override val isMuted: Boolean = raw.mute
    override val isSelfDeafen: Boolean = raw.selfDeaf
    override val isSelfMuted: Boolean = raw.selfMute
    override val isStreaming: Boolean = raw.selfStream ?: false
    override val isSuppressed: Boolean = raw.suppress
    override fun toString(): String {
        return StringBuilder("VoiceState(")
            .appendln("channel=$channel, ")
            .appendln("user=$user, ")
            .appendln("guild=$guild, ")
            .appendln("sessionId='$sessionId', ")
            .appendln("isDeafen=$isDeafen, ")
            .appendln("isMuted=$isMuted, ")
            .appendln("isSelfDeafen=$isSelfDeafen, ")
            .appendln("isSelfMuted=$isSelfMuted, ")
            .appendln("isStreaming=$isStreaming, ")
            .appendln("isSuppressed=$isSuppressed")
            .appendln(")")
            .toString()
    }
}

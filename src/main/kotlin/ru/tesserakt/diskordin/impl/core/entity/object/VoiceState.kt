package ru.tesserakt.diskordin.impl.core.entity.`object`

import ru.tesserakt.diskordin.core.data.json.response.VoiceStateResponse
import ru.tesserakt.diskordin.core.entity.IChannel
import ru.tesserakt.diskordin.core.entity.IGuild
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.core.entity.`object`.IVoiceState
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.util.Identified
import ru.tesserakt.diskordin.util.combine

class VoiceState(raw: VoiceStateResponse) : IVoiceState {
    override val channel: Identified<IChannel>? = raw.channelId?.combine { client.findChannel(it)!! }
    override val user: Identified<IUser> = raw.userId combine { client.findUser(it)!! }
    override val guild: Identified<IGuild>? = raw.guildId?.combine { client.findGuild(it)!! }
    override val sessionId: String = raw.sessionId
    override val isDeafen: Boolean = raw.deaf
    override val isMuted: Boolean = raw.mute
    override val isSelfDeafen: Boolean = raw.selfDeaf
    override val isSelfMuted: Boolean = raw.selfMute
    override val isStreaming: Boolean = raw.selfStream ?: false
    override val isSuppressed: Boolean = raw.suppress
}

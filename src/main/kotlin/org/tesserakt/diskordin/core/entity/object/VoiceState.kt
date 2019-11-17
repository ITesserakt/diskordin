package org.tesserakt.diskordin.core.entity.`object`

import org.tesserakt.diskordin.core.data.Identified
import org.tesserakt.diskordin.core.entity.IChannel
import org.tesserakt.diskordin.core.entity.IDiscordObject
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.IUser

interface IVoiceState : IDiscordObject {
    val channel: Identified<IChannel>?
    val user: Identified<IUser>
    val guild: Identified<IGuild>?
    val sessionId: String
    val isDeafen: Boolean
    val isMuted: Boolean
    val isSelfDeafen: Boolean
    val isSelfMuted: Boolean
    val isStreaming: Boolean
    val isSuppressed: Boolean
}
package org.tesserakt.diskordin.core.entity.`object`

import org.tesserakt.diskordin.core.data.DeferredIdentified
import org.tesserakt.diskordin.core.entity.IChannel
import org.tesserakt.diskordin.core.entity.IDiscordObject
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.IUser

interface IVoiceState : IDiscordObject {
    val channel: DeferredIdentified<IChannel>?
    val user: DeferredIdentified<IUser>
    val guild: DeferredIdentified<IGuild>?
    val sessionId: String
    val isDeafen: Boolean
    val isMuted: Boolean
    val isSelfDeafen: Boolean
    val isSelfMuted: Boolean
    val isStreaming: Boolean
    val isSuppressed: Boolean
}
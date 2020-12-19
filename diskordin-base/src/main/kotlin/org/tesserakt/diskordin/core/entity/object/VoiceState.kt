package org.tesserakt.diskordin.core.entity.`object`

import arrow.fx.ForIO
import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.entity.IChannel
import org.tesserakt.diskordin.core.entity.IDiscordObject
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.IUser

interface IVoiceState : IDiscordObject {
    val channel: IdentifiedF<ForIO, IChannel>?
    val user: IdentifiedF<ForIO, IUser>
    val guild: IdentifiedF<ForIO, IGuild>?
    val sessionId: String
    val isDeafen: Boolean
    val isMuted: Boolean
    val isSelfDeafen: Boolean
    val isSelfMuted: Boolean
    val isStreaming: Boolean
    val isSuppressed: Boolean
}
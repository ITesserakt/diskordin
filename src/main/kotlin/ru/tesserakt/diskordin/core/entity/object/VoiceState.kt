package ru.tesserakt.diskordin.core.entity.`object`

import ru.tesserakt.diskordin.core.entity.IChannel
import ru.tesserakt.diskordin.core.entity.IDiscordObject
import ru.tesserakt.diskordin.core.entity.IGuild
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.util.Identified

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
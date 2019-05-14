package ru.tesserakt.diskordin.core.entity.`object`

import ru.tesserakt.diskordin.core.entity.IChannel
import ru.tesserakt.diskordin.core.entity.IDiscordObject
import ru.tesserakt.diskordin.core.entity.IGuildObject
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.util.Identified

interface IVoiceState : IDiscordObject, IGuildObject {

    val channel: Identified<IChannel>?

    val user: Identified<IUser>
    val sessionId: String
    val isDeafen: Boolean
    val isMuted: Boolean
    val isSelfDeafen: Boolean
    val isSelfMuted: Boolean
    val isSuppressed: Boolean
}
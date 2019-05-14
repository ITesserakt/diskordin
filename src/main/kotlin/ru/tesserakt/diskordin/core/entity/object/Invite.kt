package ru.tesserakt.diskordin.core.entity.`object`

import ru.tesserakt.diskordin.core.entity.IChannel
import ru.tesserakt.diskordin.core.entity.IDiscordObject
import ru.tesserakt.diskordin.core.entity.IGuildObject
import ru.tesserakt.diskordin.util.Identified

interface IInvite : IDiscordObject {
    val code: String
    val url get() = "https://discord.gg/$code"

    val channel: Identified<IChannel>
    val channelType: IChannel.Type
}

interface IGuildInvite : IInvite,
    IGuildObject
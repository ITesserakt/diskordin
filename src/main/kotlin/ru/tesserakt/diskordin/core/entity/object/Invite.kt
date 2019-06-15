package ru.tesserakt.diskordin.core.entity.`object`

import ru.tesserakt.diskordin.core.entity.IChannel
import ru.tesserakt.diskordin.core.entity.IDiscordObject
import ru.tesserakt.diskordin.core.entity.IGuildObject

interface IInvite : IDiscordObject {
    val code: String
    val url get() = "https://discord.gg/$code"

    val channel: Identified<IChannel>
    val channelType: IChannel.Type

    companion object {
        inline fun <reified I : IInvite> typed(raw: InviteResponse) = when {
            raw.guild != null -> GuildInvite(raw)
            else -> Invite(raw)
        } as I
    }
}

interface IGuildInvite : IInvite, IGuildObject
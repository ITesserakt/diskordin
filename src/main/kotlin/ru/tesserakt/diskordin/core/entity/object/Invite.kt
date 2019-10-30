package ru.tesserakt.diskordin.core.entity.`object`

import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.json.response.InviteResponse
import ru.tesserakt.diskordin.core.entity.IChannel
import ru.tesserakt.diskordin.core.entity.IDiscordObject
import ru.tesserakt.diskordin.core.entity.IGuildObject
import ru.tesserakt.diskordin.impl.core.entity.`object`.GuildInvite
import ru.tesserakt.diskordin.impl.core.entity.`object`.Invite

interface IInvite : IDiscordObject {
    val code: String
    val url get() = "https://discord.gg/$code"

    val channel: Identified<IChannel>
    val channelType: IChannel.Type

    companion object {
        @Suppress("UNCHECKED_CAST")
        internal fun <I : IInvite> typed(raw: InviteResponse<I>) = when {
            raw.guild != null -> GuildInvite(raw as InviteResponse<IGuildInvite>)
            else -> Invite(raw)
        } as I
    }
}

interface IGuildInvite : IInvite, IGuildObject
package org.tesserakt.diskordin.core.entity.`object`

import org.tesserakt.diskordin.core.data.Identified
import org.tesserakt.diskordin.core.data.json.response.InviteResponse
import org.tesserakt.diskordin.core.entity.IChannel
import org.tesserakt.diskordin.core.entity.IDiscordObject
import org.tesserakt.diskordin.core.entity.IGuildObject
import org.tesserakt.diskordin.impl.core.entity.`object`.GuildInvite
import org.tesserakt.diskordin.impl.core.entity.`object`.Invite

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
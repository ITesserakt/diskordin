package org.tesserakt.diskordin.core.entity.`object`

import arrow.core.ForId
import org.tesserakt.diskordin.core.data.Identified
import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.data.json.response.InviteResponse
import org.tesserakt.diskordin.core.entity.IChannel
import org.tesserakt.diskordin.core.entity.IDiscordObject
import org.tesserakt.diskordin.core.entity.IGuildObject
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.impl.core.entity.`object`.GuildInvite
import org.tesserakt.diskordin.impl.core.entity.`object`.Invite

interface IInvite : IDiscordObject {
    val code: String
    val url get() = "https://discord.gg/$code"
    val inviter: Identified<IUser>
    val channel: IdentifiedF<ForId, IChannel>

    companion object {
        @Suppress("UNCHECKED_CAST")
        internal fun <I : IInvite> typed(raw: InviteResponse<I>) = when {
            raw.guild != null -> GuildInvite(raw as InviteResponse<IGuildInvite>)
            else -> Invite(raw)
        } as I
    }
}

interface IGuildInvite : IInvite, IGuildObject
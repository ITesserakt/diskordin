package org.tesserakt.diskordin.impl.core.entity.`object`


import arrow.core.ForId
import org.tesserakt.diskordin.core.data.Identified
import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.identifyId
import org.tesserakt.diskordin.core.data.json.response.InviteResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IChannel
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import org.tesserakt.diskordin.core.entity.`object`.IInvite
import org.tesserakt.diskordin.core.entity.client

internal open class Invite(raw: InviteResponse<IInvite>) : IInvite {
    override val code: String = raw.code
    override val inviter: Identified<IUser> = raw.inviter.id identifyId { raw.inviter.unwrap() }
    override val channel: IdentifiedF<ForId, IChannel> = raw.channel.id.identifyId { raw.channel.unwrap() }
    override fun toString(): String {
        return StringBuilder("Invite(")
            .appendLine("code='$code', ")
            .appendLine("channel=$channel, ")
            .appendLine("inviter=$inviter")
            .appendLine(")")
            .toString()
    }
}

internal class GuildInvite(raw: InviteResponse<IGuildInvite>) : Invite(raw), IGuildInvite {
    override val guild = raw.guild!!.id.identify<IGuild> {
        client.getGuild(it)
    }

    override fun toString(): String {
        return StringBuilder("GuildInvite(")
            .appendLine("guild=$guild")
            .appendLine(") ${super.toString()}")
            .toString()
    }
}
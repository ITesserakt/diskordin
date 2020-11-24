package org.tesserakt.diskordin.impl.core.entity.`object`


import org.tesserakt.diskordin.core.data.Identified
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
import java.time.Instant

internal open class Invite(raw: InviteResponse<IInvite>) : IInvite {
    override val code: String = raw.code
    override val inviter: Identified<IUser>? = raw.inviter?.id?.identifyId { raw.inviter.unwrap() }
    override val channel: Identified<IChannel>? = raw.channel?.id?.identifyId { raw.channel.unwrap() }
    override val uses: Int? = raw.uses
    override val maxUses: Int? = raw.maxUses
    override val maxAge: Int? = raw.maxAge
    override val temporary: Boolean? = raw.temporary
    override val createdAt: Instant? = raw.createdAt

    override fun toString(): String {
        return "Invite(code='$code', inviter=$inviter, channel=$channel, uses=$uses, maxUses=$maxUses, maxAge=$maxAge, temporary=$temporary, createdAt=$createdAt)"
    }
}

internal class GuildInvite(raw: InviteResponse<IGuildInvite>) : Invite(raw), IGuildInvite {
    override val guild = raw.guild!!.id.identify<IGuild> {
        client.getGuild(it)
    }

    override fun toString(): String {
        return "GuildInvite(guild=$guild) " +
                "\n   ${super.toString()}"
    }
}
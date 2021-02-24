package org.tesserakt.diskordin.impl.core.entity.`object`


import org.tesserakt.diskordin.core.data.EagerIdentified
import org.tesserakt.diskordin.core.data.deferred
import org.tesserakt.diskordin.core.data.eager
import org.tesserakt.diskordin.core.data.json.response.InviteResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IChannel
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import org.tesserakt.diskordin.core.entity.`object`.IInvite
import org.tesserakt.diskordin.core.entity.client

internal open class Invite(raw: InviteResponse<IInvite>) : IInvite {
    override val code: String = raw.code
    override val inviter: EagerIdentified<IUser>? = raw.inviter?.id?.eager { raw.inviter.unwrap() }
    override val channel: EagerIdentified<IChannel>? = raw.channel?.id?.eager { raw.channel.unwrap() }
    override val uses: Int? = raw.uses
    override val maxUses: Int? = raw.maxUses
    override val maxAge: Int? = raw.maxAge
    override val temporary: Boolean? = raw.temporary
    override val createdAt = raw.createdAt

    override fun toString(): String {
        return "Invite(code='$code', inviter=$inviter, channel=$channel, uses=$uses, maxUses=$maxUses, maxAge=$maxAge, temporary=$temporary, createdAt=$createdAt)"
    }
}

internal class GuildInvite(raw: InviteResponse<IGuildInvite>) : Invite(raw), IGuildInvite {
    override val guild = raw.guild!!.id deferred {
        client.getGuild(it)
    }

    override fun toString(): String {
        return "GuildInvite(guild=$guild) " +
                "\n   ${super.toString()}"
    }
}
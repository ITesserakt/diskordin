package org.tesserakt.diskordin.impl.core.entity.`object`


import org.tesserakt.diskordin.core.data.Identified
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.InviteResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IChannel
import org.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import org.tesserakt.diskordin.core.entity.`object`.IInvite
import org.tesserakt.diskordin.core.entity.client

open class Invite(raw: InviteResponse<IInvite>) : IInvite {
    override val code: String = raw.code
    override val channel: Identified<IChannel> = raw.channel.id identify { raw.channel.unwrap() }
    override val channelType: IChannel.Type = IChannel.Type.values().first { it.ordinal == raw.channel.type }
    override fun toString(): String {
        return StringBuilder("Invite(")
            .appendln("code='$code', ")
            .appendln("channel=$channel, ")
            .appendln("channelType=$channelType")
            .appendln(")")
            .toString()
    }
}

class GuildInvite(raw: InviteResponse<IGuildInvite>) : Invite(raw), IGuildInvite {
    override val guild = raw.guild!!.id identify {
        client.getGuild(it).bind()
    }

    override fun toString(): String {
        return StringBuilder("GuildInvite(")
            .appendln("guild=$guild")
            .appendln(") ${super.toString()}")
            .toString()
    }
}
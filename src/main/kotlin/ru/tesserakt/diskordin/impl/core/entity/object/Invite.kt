package ru.tesserakt.diskordin.impl.core.entity.`object`


import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.identify
import ru.tesserakt.diskordin.core.data.json.response.InviteResponse
import ru.tesserakt.diskordin.core.data.json.response.unwrap
import ru.tesserakt.diskordin.core.entity.IChannel
import ru.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import ru.tesserakt.diskordin.core.entity.`object`.IInvite
import ru.tesserakt.diskordin.core.entity.client

open class Invite(raw: InviteResponse<IInvite>) : IInvite {
    override val code: String = raw.code
    override val channel: Identified<IChannel> = raw.channel.id identify { raw.channel.unwrap() }
    override val channelType: IChannel.Type = IChannel.Type.values().first { it.ordinal == raw.channel.type }
    override fun toString(): String {
        return "Invite(code='$code', channel=$channel, channelType=$channelType)"
    }
}

class GuildInvite(raw: InviteResponse<IGuildInvite>) : Invite(raw), IGuildInvite {
    override val guild = raw.guild!!.id identify {
        client.getGuild(it).bind()
    }

    override fun toString(): String {
        return "GuildInvite(guild=$guild) ${super.toString()}"
    }
}
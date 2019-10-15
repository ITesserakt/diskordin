package ru.tesserakt.diskordin.impl.core.entity.`object`


import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.InviteResponse
import ru.tesserakt.diskordin.core.entity.IChannel
import ru.tesserakt.diskordin.core.entity.IGuild
import ru.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import ru.tesserakt.diskordin.core.entity.`object`.IInvite
import ru.tesserakt.diskordin.impl.core.entity.Guild
import ru.tesserakt.diskordin.util.Identified

open class Invite(raw: InviteResponse) : IInvite {
    override val code: String = raw.code
    override val channel: Identified<IChannel> =
        Identified(raw.channel.id.asSnowflake()) { raw.channel.unwrap<IChannel>() }
    override val channelType: IChannel.Type = IChannel.Type.values().first { it.ordinal == raw.channel.type }
}

class GuildInvite(raw: InviteResponse) : Invite(raw), IGuildInvite {
    override val guild: Identified<IGuild> = run {
        requireNotNull(raw.guild) { "Not a guild invite" }
        Identified(raw.guild.id.asSnowflake()) { Guild(raw.guild) }
    }
}
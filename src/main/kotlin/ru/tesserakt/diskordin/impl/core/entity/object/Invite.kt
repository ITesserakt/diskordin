package ru.tesserakt.diskordin.impl.core.entity.`object`

import kotlinx.coroutines.async
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import ru.tesserakt.diskordin.Diskordin
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.InviteResponse
import ru.tesserakt.diskordin.core.entity.IChannel
import ru.tesserakt.diskordin.core.entity.IGuild
import ru.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import ru.tesserakt.diskordin.core.entity.`object`.IInvite
import ru.tesserakt.diskordin.impl.core.entity.Guild
import ru.tesserakt.diskordin.util.Identified

open class Invite(raw: InviteResponse, final override val kodein: Kodein = Diskordin.kodein) : IInvite {
    override val code: String = raw.code
    override val channel: Identified<IChannel> = Identified(raw.channel.id.asSnowflake()) {
        client.coroutineScope.async {
            IChannel.typed<IChannel>(raw.channel)
        }
    }
    override val channelType: IChannel.Type = IChannel.Type.of(raw.channel.type)
    override val client: IDiscordClient by instance()
}

class GuildInvite(raw: InviteResponse) : Invite(raw), IGuildInvite {
    override val guild: Identified<IGuild> = run {
        requireNotNull(raw.guild) { "Not a guild invite" }
        Identified(raw.guild.id.asSnowflake()) {
            client.coroutineScope.async {
                Guild(raw.guild)
            }
        }
    }
}
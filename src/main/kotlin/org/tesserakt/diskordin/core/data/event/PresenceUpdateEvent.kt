@file:Suppress("unused")

package org.tesserakt.diskordin.core.data.event

import arrow.core.ForId
import arrow.fx.ForIO
import arrow.fx.coroutines.stream.Chunk
import arrow.fx.coroutines.stream.Stream
import arrow.fx.coroutines.stream.filterOption
import org.tesserakt.diskordin.core.data.event.guild.IGuildEvent
import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.identifyId
import org.tesserakt.diskordin.core.data.invoke
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.cache
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.PresenceUpdate
import kotlin.time.ExperimentalTime

class PresenceUpdateEvent(raw: PresenceUpdate) : IGuildEvent<ForIO>, IUserEvent<ForId> {
    override val guild = raw.guildId.identify<IGuild> { client.getGuild(it) }
    val roles = Stream.chunk(Chunk.array(raw.roles))
        .effectMap { guild().getRole(it) }
        .filterOption()

    @ExperimentalTime
    val game = raw.game?.unwrap()
    val status = UserStatus.valueOf(raw.status.toUpperCase())

    @ExperimentalTime
    val activities = raw.activities.map { it.unwrap() }
    val clientStatus = when {
        raw.clientStatus.desktop != null ->
            ClientStatus.Desktop(UserStatus.valueOf(raw.clientStatus.desktop.toUpperCase()))
        raw.clientStatus.mobile != null ->
            ClientStatus.Mobile(UserStatus.valueOf(raw.clientStatus.mobile.toUpperCase()))
        raw.clientStatus.web != null ->
            ClientStatus.Web(UserStatus.valueOf(raw.clientStatus.web.toUpperCase()))
        else -> null
    }
    override val user = raw.user.id.identifyId { raw.user.unwrap() }

    init {
        cache.putIfAbsent(user.id, user())
    }
}
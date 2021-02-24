@file:Suppress("unused")

package org.tesserakt.diskordin.core.data.event

import org.tesserakt.diskordin.core.data.deferred
import org.tesserakt.diskordin.core.data.event.guild.IGuildEvent
import org.tesserakt.diskordin.core.data.identified
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.PresenceUpdate
import kotlin.time.ExperimentalTime

class PresenceUpdateEvent(raw: PresenceUpdate) : IGuildEvent.Deferred, IUserEvent.Eager {
    override val guild = raw.guildId deferred { client.getGuild(raw.guildId) }
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
    override val user = raw.user.unwrap().identified()
}
@file:Suppress("unused")

package org.tesserakt.diskordin.core.data.event

import org.tesserakt.diskordin.core.data.deferred
import org.tesserakt.diskordin.core.data.event.guild.IGuildEvent
import org.tesserakt.diskordin.core.data.identified
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.PresenceUpdate
import java.util.*
import kotlin.time.ExperimentalTime

class PresenceUpdateEvent(raw: PresenceUpdate) : IGuildEvent.Deferred, IUserEvent.Eager {
    override val guild = raw.guildId deferred { client.getGuild(raw.guildId) }
    val status = UserStatus.valueOf(raw.status.uppercase(Locale.getDefault()))

    @ExperimentalTime
    val activities = raw.activities.map { it.unwrap() }
    val clientStatus = when {
        raw.clientStatus.desktop != null ->
            ClientStatus.Desktop(UserStatus.valueOf(raw.clientStatus.desktop.uppercase(Locale.getDefault())))
        raw.clientStatus.mobile != null ->
            ClientStatus.Mobile(UserStatus.valueOf(raw.clientStatus.mobile.uppercase(Locale.getDefault())))
        raw.clientStatus.web != null ->
            ClientStatus.Web(UserStatus.valueOf(raw.clientStatus.web.uppercase(Locale.getDefault())))
        else -> null
    }
    override val user = raw.user.unwrap().identified()
}
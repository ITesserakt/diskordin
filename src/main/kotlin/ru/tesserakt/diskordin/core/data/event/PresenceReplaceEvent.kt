@file:Suppress("unused")

package ru.tesserakt.diskordin.core.data.event

import arrow.core.k
import arrow.fx.IO
import arrow.fx.extensions.fx
import ru.tesserakt.diskordin.core.data.identify
import ru.tesserakt.diskordin.core.data.json.response.unwrap
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.PresenceUpdate
import kotlin.time.ExperimentalTime

class PresenceReplaceEvent(raw: PresenceUpdate) : IEvent {
    val guild = raw.guildId identify { client.getGuild(it).bind() }
    val roles = IO.fx {
        raw.roles.map { guild().bind().getRole(it).bind() }.k()
    }
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
    val user = raw.user.id identify { raw.user.unwrap() }
}

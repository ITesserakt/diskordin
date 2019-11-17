@file:Suppress("unused")

package org.tesserakt.diskordin.core.data.event

import arrow.core.k
import arrow.fx.IO
import arrow.fx.extensions.fx
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.PresenceUpdate
import kotlin.time.ExperimentalTime

class PresenceUpdateEvent(raw: PresenceUpdate) : IEvent {
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

sealed class ClientStatus {
    data class Desktop(val status: UserStatus) : ClientStatus()
    data class Mobile(val status: UserStatus) : ClientStatus()
    data class Web(val status: UserStatus) : ClientStatus()
}

enum class UserStatus {
    IDLE,
    ONLINE,
    DND,
    OFFLINE,
    INVISIBLE;
}

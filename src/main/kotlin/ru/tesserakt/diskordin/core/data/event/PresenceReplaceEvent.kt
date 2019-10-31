package ru.tesserakt.diskordin.core.data.event

import kotlinx.coroutines.flow.flow
import ru.tesserakt.diskordin.core.data.combine
import ru.tesserakt.diskordin.core.data.json.response.unwrap
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.PresenceUpdate
import kotlin.time.ExperimentalTime

class PresenceReplaceEvent(raw: PresenceUpdate) : IEvent {
    val guild = raw.guildId combine { client.getGuild(it) }
    val roles = flow {
        raw.roles.map { guild().getRole(it) }.forEach { emit(it) }
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
    val user = raw.user.id combine { raw.user.unwrap() }
}

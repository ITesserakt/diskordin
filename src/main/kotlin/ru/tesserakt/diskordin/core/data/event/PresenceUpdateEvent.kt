package ru.tesserakt.diskordin.core.data.event

import kotlinx.coroutines.flow.flow
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.gateway.json.events.PresenceUpdate
import ru.tesserakt.diskordin.util.combine
import kotlin.time.ExperimentalTime

class PresenceUpdateEvent(raw: PresenceUpdate) : IEvent {
    val guild = raw.guildId combine { client.findGuild(it)!! }
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

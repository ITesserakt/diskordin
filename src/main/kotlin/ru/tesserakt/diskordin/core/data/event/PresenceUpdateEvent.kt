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
            ClientStatus.Desktop(raw.clientStatus.desktop)
        raw.clientStatus.mobile != null ->
            ClientStatus.Mobile(raw.clientStatus.mobile)
        raw.clientStatus.web != null ->
            ClientStatus.Web(raw.clientStatus.web)
        else -> null
    }
    val user = raw.user.id combine { raw.user.unwrap() }
}

sealed class ClientStatus {
    data class Desktop(val platform: String) : ClientStatus()
    data class Mobile(val platform: String) : ClientStatus()
    data class Web(val platform: String) : ClientStatus()
}

enum class UserStatus {
    IDLE,
    ONLINE,
    DND,
    OFFLINE,
    INVISIBLE;
}

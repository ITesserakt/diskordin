package ru.tesserakt.diskordin.gateway.json.events

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.response.ActivityResponse
import ru.tesserakt.diskordin.core.data.json.response.IDUserResponse
import ru.tesserakt.diskordin.gateway.json.IRawEvent

data class PresenceUpdate(
    val user: IDUserResponse,
    val roles: Array<Snowflake>,
    val game: ActivityResponse?,
    val guildId: Snowflake,
    val status: String,
    val activities: Array<ActivityResponse>,
    val clientStatus: ClientStatus
) : IRawEvent {
    data class ClientStatus(
        val desktop: String?,
        val mobile: String?,
        val web: String?
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PresenceUpdate) return false

        if (user != other.user) return false
        if (!roles.contentEquals(other.roles)) return false
        if (game != other.game) return false
        if (guildId != other.guildId) return false
        if (status != other.status) return false
        if (!activities.contentEquals(other.activities)) return false
        if (clientStatus != other.clientStatus) return false

        return true
    }

    override fun hashCode(): Int {
        var result = user.hashCode()
        result = 31 * result + roles.contentHashCode()
        result = 31 * result + (game?.hashCode() ?: 0)
        result = 31 * result + guildId.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + activities.contentHashCode()
        result = 31 * result + clientStatus.hashCode()
        return result
    }
}

package org.tesserakt.diskordin.gateway.json.events

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.ActivityResponse
import org.tesserakt.diskordin.core.data.json.response.IDUserResponse
import org.tesserakt.diskordin.gateway.json.IRawEvent

data class PresenceUpdate(
    val user: IDUserResponse,
    val roles: List<Snowflake>,
    val game: ActivityResponse?,
    val guildId: Snowflake,
    val status: String,
    val activities: List<ActivityResponse>,
    val clientStatus: ClientStatus
) : IRawEvent {
    data class ClientStatus(
        val desktop: String?,
        val mobile: String?,
        val web: String?
    )
}

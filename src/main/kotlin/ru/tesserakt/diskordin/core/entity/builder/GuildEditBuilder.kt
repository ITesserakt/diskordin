package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.request.GuildEditRequest

class GuildEditBuilder : AuditLogging<GuildEditRequest>() {
    override fun create(): GuildEditRequest = GuildEditRequest(
        name,
        region,
        verificationLevel,
        defaultMessageNotifications,
        explicitContentFilter,
        afkChannelId,
        afkTimeout,
        icon,
        ownerId,
        splash,
        systemChannelId
    )

    var name: String? = null
    var region: String? = null
    var verificationLevel: Int? = null
    var defaultMessageNotifications: Int? = null
    var explicitContentFilter: Int? = null
    var afkChannelId: Snowflake? = null
    var afkTimeout: Int? = null
    var icon: String? = null
    var ownerId: Snowflake? = null
    var splash: String? = null
    var systemChannelId: Snowflake? = null
    override var reason: String? = null
}
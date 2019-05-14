package ru.tesserakt.diskordin.core.entity.builder

import ru.tesserakt.diskordin.core.data.json.request.GuildCreateRequest
import ru.tesserakt.diskordin.core.entity.VerificationLevel
import ru.tesserakt.diskordin.impl.core.entity.`object`.Region
import kotlin.properties.Delegates

class GuildCreateBuilder : IBuilder<GuildCreateRequest> {
    lateinit var name: String
    lateinit var region: Region
    lateinit var icon: String
    lateinit var verificationLevel: VerificationLevel
    var defaultMessageNotifications: Int by Delegates.notNull() //TODO: Add enum
    var explicitContentFilter: Int by Delegates.notNull() //TODO same
    lateinit var roles: Array<RoleCreateBuilder.() -> Unit>
    lateinit var channels: Array<PartialChannelCreateBuilder.() -> Unit>

    override fun create(): GuildCreateRequest = GuildCreateRequest(
        name,
        region.name,
        icon,
        verificationLevel.ordinal,
        defaultMessageNotifications,
        explicitContentFilter,
        roles.map {
            RoleCreateBuilder().apply(it).create()
        }.toTypedArray(),
        channels.map {
            PartialChannelCreateBuilder().apply(it).create()
        }.toTypedArray()
    )
}
@file:Suppress("MemberVisibilityCanBePrivate")

package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.json.request.GuildCreateRequest
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.impl.core.entity.`object`.Region

class GuildCreateBuilder : BuilderBase<GuildCreateRequest>() {
    lateinit var name: String
    lateinit var region: Region
    lateinit var icon: String
    lateinit var verificationLevel: IGuild.VerificationLevel
    lateinit var defaultMessageNotifications: IGuild.DefaultMessageNotificationLevel
    lateinit var explicitContentFilter: IGuild.ExplicitContentFilter
    lateinit var roles: Array<RoleCreateBuilder.() -> Unit>
    lateinit var channels: Array<PartialChannelCreateBuilder.() -> Unit>

    override fun create(): GuildCreateRequest = GuildCreateRequest(
        name,
        region.name,
        icon,
        verificationLevel.ordinal,
        defaultMessageNotifications.ordinal,
        explicitContentFilter.ordinal,
        roles.map {
            RoleCreateBuilder().apply(it).create()
        }.toTypedArray(),
        channels.map {
            PartialChannelCreateBuilder().apply(it).create()
        }.toTypedArray()
    )
}
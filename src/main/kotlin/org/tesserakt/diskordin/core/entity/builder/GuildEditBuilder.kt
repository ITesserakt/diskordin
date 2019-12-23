package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.GuildEditRequest
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.`object`.IRegion

@RequestBuilder
@Suppress("NOTHING_TO_INLINE", "unused")
class GuildEditBuilder : AuditLogging<GuildEditRequest>() {
    override fun create(): GuildEditRequest = GuildEditRequest(
        name,
        region,
        verificationLevel?.ordinal,
        defaultMessageNotifications?.ordinal,
        explicitContentFilter?.ordinal,
        afkChannelId,
        afkTimeout,
        icon,
        ownerId,
        splash,
        systemChannelId
    )

    private var name: String? = null
    private var region: String? = null
    private var verificationLevel: IGuild.VerificationLevel? = null
    private var defaultMessageNotifications: IGuild.DefaultMessageNotificationLevel? = null
    private var explicitContentFilter: IGuild.ExplicitContentFilter? = null
    private var afkChannelId: Snowflake? = null
    private var afkTimeout: Int? = null
    private var icon: String? = null
    private var ownerId: Snowflake? = null
    private var splash: String? = null
    private var systemChannelId: Snowflake? = null

    operator fun Name.unaryPlus() {
        name = this.v
    }

    operator fun Region.unaryPlus() {
        region = this.v
    }

    operator fun IGuild.VerificationLevel.unaryPlus() {
        verificationLevel = this
    }

    operator fun IGuild.DefaultMessageNotificationLevel.unaryPlus() {
        defaultMessageNotifications = this
    }

    operator fun IGuild.ExplicitContentFilter.unaryPlus() {
        explicitContentFilter = this
    }

    operator fun AfkChannel.unaryPlus() {
        afkChannelId = this.v
    }


    operator fun AfkTimeout.unaryPlus() {
        afkTimeout = this.v
    }

    operator fun IconURL.unaryPlus() {
        icon = this.v
    }

    operator fun Owner.unaryPlus() {
        ownerId = this.v
    }

    operator fun SplashURL.unaryPlus() {
        splash = this.v
    }

    operator fun SystemChannel.unaryPlus() {
        systemChannelId = this.v
    }

    inline fun GuildEditBuilder.name(name: String) = Name(name)
    inline fun GuildEditBuilder.region(region: IRegion) = Region(region.name)
    inline fun GuildEditBuilder.verificationLevel(level: IGuild.VerificationLevel) = level
    inline fun GuildEditBuilder.messageNotificationLevel(level: IGuild.DefaultMessageNotificationLevel) = level
    inline fun GuildEditBuilder.explicitContentFilter(level: IGuild.ExplicitContentFilter) = level
    inline fun GuildEditBuilder.afkChannel(id: Snowflake) = AfkChannel(id)
    inline fun GuildEditBuilder.afkTimeout(timeout: Int) = AfkTimeout(timeout)
    inline fun GuildEditBuilder.icon(url: String) = IconURL(url)
    inline fun GuildEditBuilder.owner(id: Snowflake) = Owner(id)
    inline fun GuildEditBuilder.splash(url: String) = SplashURL(url)
    inline fun GuildEditBuilder.systemChannel(id: Snowflake) = SystemChannel(id)
}
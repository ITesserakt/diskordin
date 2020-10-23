@file:Suppress("MemberVisibilityCanBePrivate")

package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.GuildCreateRequest
import org.tesserakt.diskordin.core.entity.IChannel
import org.tesserakt.diskordin.core.entity.IGuild
import java.awt.Color

@Suppress("unused", "NOTHING_TO_INLINE")
@RequestBuilder
class GuildCreateBuilder(val name: String) : BuilderBase<GuildCreateRequest>() {
    private var explicitContentFilter: IGuild.ExplicitContentFilter? = null
    private var defaultMessageNotifications: IGuild.DefaultMessageNotificationLevel? = null
    private var verificationLevel: IGuild.VerificationLevel? = null
    private var icon: IconURL? = null
    private var region: String? = null
    private val roles: MutableList<RoleCreateBuilder> = mutableListOf()
    private val channels: MutableList<PartialChannelCreateBuilder> = mutableListOf()
    private var afkChannel: AfkChannel? = null
    private var afkTimeout: AfkTimeout? = null
    private var systemChannel: SystemChannel? = null

    operator fun RoleCreateBuilder.unaryPlus() {
        this@GuildCreateBuilder.roles += this
    }

    operator fun PartialChannelCreateBuilder.unaryPlus() {
        this@GuildCreateBuilder.channels += this
    }

    operator fun IGuild.ExplicitContentFilter.unaryPlus() {
        explicitContentFilter = this
    }

    operator fun IGuild.DefaultMessageNotificationLevel.unaryPlus() {
        defaultMessageNotifications = this
    }

    operator fun IGuild.VerificationLevel.unaryPlus() {
        verificationLevel = this
    }

    operator fun IconURL.unaryPlus() {
        icon = this
    }

    operator fun String.unaryPlus() {
        region = this
    }

    operator fun AfkChannel.unaryPlus() {
        afkChannel = this
    }

    operator fun AfkTimeout.unaryPlus() {
        afkTimeout = this
    }

    operator fun SystemChannel.unaryPlus() {
        systemChannel = this
    }

    inline fun GuildCreateBuilder.role(
        name: String,
        color: Color,
        builder: RoleCreateBuilder.() -> Unit
    ) = RoleCreateBuilder(name, color).apply(builder)

    inline fun GuildCreateBuilder.channel(name: String, type: IChannel.Type) =
        PartialChannelCreateBuilder(name, type)

    inline fun GuildCreateBuilder.region(value: String) = value
    inline fun GuildCreateBuilder.icon(value: String) = IconURL(value)
    inline fun GuildCreateBuilder.verificationLevel(level: IGuild.VerificationLevel) = level
    inline fun GuildCreateBuilder.messageNotifications(level: IGuild.DefaultMessageNotificationLevel) = level
    inline fun GuildCreateBuilder.explicitContentFilter(level: IGuild.ExplicitContentFilter) = level
    inline fun GuildCreateBuilder.afkChannel(id: Snowflake) = AfkChannel(id)
    inline fun GuildCreateBuilder.akfTimeout(timeout: Int) = AfkTimeout(timeout)
    inline fun GuildCreateBuilder.systemChannel(id: Snowflake) = SystemChannel(id)

    override fun create(): GuildCreateRequest = GuildCreateRequest(
        name,
        region,
        icon?.v,
        verificationLevel?.ordinal,
        defaultMessageNotifications?.ordinal,
        explicitContentFilter?.ordinal,
        roles.map { it.create() },
        channels.map { it.create() },
        afkChannel?.v,
        afkTimeout?.v,
        systemChannel?.v
    )
}
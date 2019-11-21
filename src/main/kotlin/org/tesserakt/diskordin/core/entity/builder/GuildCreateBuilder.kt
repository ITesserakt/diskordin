@file:Suppress("MemberVisibilityCanBePrivate")

package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.json.request.GuildCreateRequest
import org.tesserakt.diskordin.core.entity.IChannel
import org.tesserakt.diskordin.core.entity.IGuild
import java.awt.Color

@Suppress("unused")
@RequestBuilder
class GuildCreateBuilder(
    var name: String, val region: String, val icon: String,
    val verificationLevel: IGuild.VerificationLevel,
    val defaultMessageNotifications: IGuild.DefaultMessageNotificationLevel,
    val explicitContentFilter: IGuild.ExplicitContentFilter
) : BuilderBase<GuildCreateRequest>() {
    private val roles: MutableList<RoleCreateBuilder> = mutableListOf()
    private val channels: MutableList<PartialChannelCreateBuilder> = mutableListOf()

    operator fun RoleCreateBuilder.unaryPlus() {
        this@GuildCreateBuilder.roles += this
    }

    operator fun PartialChannelCreateBuilder.unaryPlus() {
        this@GuildCreateBuilder.channels += this
    }

    inline fun GuildCreateBuilder.role(
        name: String,
        color: Color,
        builder: RoleCreateBuilder.() -> Unit
    ) = RoleCreateBuilder(name, color).apply(builder)

    @Suppress("NOTHING_TO_INLINE")
    inline fun GuildCreateBuilder.channel(name: String, type: IChannel.Type) =
        PartialChannelCreateBuilder(name, type)

    override fun create(): GuildCreateRequest = GuildCreateRequest(
        name,
        region,
        icon,
        verificationLevel.ordinal,
        defaultMessageNotifications.ordinal,
        explicitContentFilter.ordinal,
        roles.map { it.create() }.toTypedArray(),
        channels.map { it.create() }.toTypedArray()
    )
}
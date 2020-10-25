package org.tesserakt.diskordin.core.entity

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.`object`.IGuildWidgetSettings
import org.tesserakt.diskordin.core.entity.`object`.IInvite
import org.tesserakt.diskordin.core.entity.builder.GuildWidgetEditBuilder
import org.tesserakt.diskordin.core.entity.builder.PresenceBuilder

interface IGuildWidget : INamed {
    val instantInviteUrl: IInvite
    val id: Snowflake
    val channels: List<IChannelPreview>
    val members: List<IMemberPreview>
    val presenceCount: Int

    suspend fun getSettings(): IGuildWidgetSettings
    suspend fun edit(builder: GuildWidgetEditBuilder.() -> Unit): IGuildWidgetSettings

    interface IChannelPreview : IEntity, INamed, IPreviewed<IGuildChannel> {
        val position: Int
    }

    interface IMemberPreview : INamed {
        val id: Long
        val discriminator: Short
        val avatar: String?
        val avatarUrl: String?
        val statusType: PresenceBuilder.StatusType
    }
}
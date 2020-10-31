package org.tesserakt.diskordin.impl.core.entity

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.invoke
import org.tesserakt.diskordin.core.data.json.response.GuildWidgetResponse
import org.tesserakt.diskordin.core.data.json.response.InviteResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.IGuildChannel
import org.tesserakt.diskordin.core.entity.IGuildWidget
import org.tesserakt.diskordin.core.entity.`object`.IGuildWidgetSettings
import org.tesserakt.diskordin.core.entity.`object`.IInvite
import org.tesserakt.diskordin.core.entity.builder.GuildWidgetEditBuilder
import org.tesserakt.diskordin.core.entity.builder.PresenceBuilder
import org.tesserakt.diskordin.core.entity.builder.build
import org.tesserakt.diskordin.core.entity.client

class GuildWidget(raw: GuildWidgetResponse) : IGuildWidget {
    override val instantInviteUrl: IInvite = IInvite.typed(
        InviteResponse(
            raw.instantInvite.split('/')
                .let { if (it.last().isEmpty()) it.dropLast(1).last() else it.last() } // discord.com/invites/{code}/
        )
    )
    override val id: Snowflake = raw.id
    override val channels: List<IGuildWidget.IChannelPreview> = raw.channels.map { it.unwrap(id) }
    override val members: List<IGuildWidget.IMemberPreview> = raw.members.map { it.unwrap(id) }
    override val presenceCount: Int = raw.presenceCount
    override val name: String = raw.name

    override suspend fun getSettings(): IGuildWidgetSettings = client.rest.call {
        guildService.getGuildWidgetSettings(id)
    }

    override suspend fun edit(builder: GuildWidgetEditBuilder.() -> Unit): IGuildWidgetSettings {
        val previousSettings = getSettings()
        return client.rest.call {
            guildService.modifyGuildWidget(id, builder.build { GuildWidgetEditBuilder(previousSettings.isEnabled) })
        }
    }

    override fun toString(): String {
        return "GuildWidget(instantInviteUrl=$instantInviteUrl, channels=$channels, members=$members, presenceCount=$presenceCount, id=$id, name='$name')"
    }

    class ChannelPreview(raw: GuildWidgetResponse.ChannelResponse, guildId: Snowflake) : IGuildWidget.IChannelPreview {
        override val id: Snowflake = raw.id
        override val name: String = raw.name
        private val guild = guildId.identify<IGuild> {
            client.getGuild(it)
        }

        override suspend fun extend(): IGuildChannel = guild().getChannel(id)

        override fun toString(): String {
            return "ChannelPreview(id=$id, name='$name', guild=$guild, position=$position)"
        }

        override val position: Int = raw.position
    }

    class MemberPreview(raw: GuildWidgetResponse.MemberResponse, private val guildId: Snowflake) :
        IGuildWidget.IMemberPreview {
        override val id: Long = raw.id
        override val name: String = raw.username

        override fun toString(): String {
            return "MemberPreview(guildId=$guildId, id=$id, name='$name', discriminator=$discriminator, avatar=$avatar, avatarUrl=$avatarUrl, statusType=$statusType)"
        }

        override val discriminator: Short = raw.discriminator
        override val avatar: String? = raw.avatar
        override val avatarUrl: String? = raw.avatarUrl
        override val statusType: PresenceBuilder.StatusType = PresenceBuilder.StatusType.values()
            .first { it.name.toLowerCase() == raw.status }
    }
}
package org.tesserakt.diskordin.impl.core.entity

import arrow.core.Nel
import arrow.core.NonEmptyList
import arrow.fx.ForIO
import mu.KotlinLogging
import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.data.Permissions
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.ChannelResponse
import org.tesserakt.diskordin.core.data.json.response.ImageResponse
import org.tesserakt.diskordin.core.data.json.response.UnwrapContext
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import org.tesserakt.diskordin.core.entity.`object`.IPermissionOverwrite
import org.tesserakt.diskordin.core.entity.builder.PermissionEditBuilder
import org.tesserakt.diskordin.core.entity.builder.TextChannelEditBuilder
import org.tesserakt.diskordin.core.entity.builder.VoiceChannelEditBuilder
import org.tesserakt.diskordin.core.entity.builder.instance
import org.tesserakt.diskordin.rest.stream
import org.tesserakt.diskordin.util.enums.and
import org.tesserakt.diskordin.util.enums.not

internal sealed class Channel(raw: ChannelResponse<IChannel>) : IChannel {
    private val logger = KotlinLogging.logger { }

    final override val type: IChannel.Type = IChannel.Type.values().first { it.ordinal == raw.type }

    final override val id: Snowflake = raw.id

    final override val mention: String = "<#${id.asString()}>"

    final override suspend fun delete(reason: String?) = rest.call {
        channelService.deleteChannel(id, reason)
    }.let { }

    override fun toString(): String {
        return "Channel(type=$type, id=$id, mention='$mention', invites=$invites)"
    }

    override val invites = rest.stream {
        channelService.getChannelInvites(id)
    }
}

internal sealed class GuildChannel(raw: ChannelResponse<IGuildChannel>) : Channel(raw), IGuildChannel {
    final override val position: Int = raw.position!!

    final override val permissionOverwrites = raw.permission_overwrites!!.map { it.unwrap() }

    final override val parentCategory: Snowflake? = raw.parent_id

    final override val guild = raw.guild_id!!.identify<IGuild> {
        client.getGuild(it)
    }

    final override val name: String = raw.name!!

    final override val invites = rest.stream {
        channelService.getChannelInvites(id)
    }.map { it as IGuildInvite }

    final override suspend fun editPermissions(
        overwrite: IPermissionOverwrite,
        type: IPermissionOverwrite.Type,
        allowed: Permissions,
        denied: Permissions,
        reason: String?
    ) = rest.effect {
        val instance = PermissionEditBuilder(type, allowed, denied).apply {
            if (reason != null)
                +reason(reason)
        }
        channelService.editChannelPermissions(id, overwrite.computeCode(), instance.create(), instance.reason)
    }

    final override suspend fun removePermissions(toRemove: IPermissionOverwrite, reason: String?) = rest.effect {
        channelService.deleteChannelPermissions(id, (toRemove.allowed and !toRemove.denied).code, reason)
    }

    override fun toString(): String {
        return "GuildChannel(position=$position, permissionOverwrites=$permissionOverwrites, parentCategory=$parentCategory, guild=$guild, name='$name', invites=$invites) " +
                "\n   ${super.toString()}"
    }
}

internal class TextChannel(raw: ChannelResponse<ITextChannel>) : GuildChannel(raw), ITextChannel {
    override val pins = rest.stream {
        channelService.getPinnedMessages(id)
    }

    override val isNSFW: Boolean = raw.nsfw!!

    override val topic: String? = raw.topic

    @ExperimentalUnsignedTypes
    override val rateLimit: UShort = raw.rate_limit_per_user!!.toUShort()

    override suspend fun edit(builder: TextChannelEditBuilder.() -> Unit) = rest.call {
        val inst = builder.instance(::TextChannelEditBuilder)
        channelService.editChannel(id, inst.create(), inst.reason)
    } as ITextChannel

    @ExperimentalUnsignedTypes
    override fun toString(): String {
        return "TextChannel(isNSFW=$isNSFW, topic=$topic, rateLimit=$rateLimit) " +
                "\n   ${super.toString()}"
    }
}

internal class VoiceChannel(raw: ChannelResponse<IVoiceChannel>) : GuildChannel(raw), IVoiceChannel {
    override val bitrate: Int = raw.bitrate!!

    override val userLimit: Int = raw.user_limit!!

    override suspend fun edit(builder: VoiceChannelEditBuilder.() -> Unit) = rest.call {
        val inst = builder.instance(::VoiceChannelEditBuilder)
        channelService.editChannel(id, inst.create(), inst.reason)
    } as IVoiceChannel

    override fun toString(): String {
        return "VoiceChannel(bitrate=$bitrate, userLimit=$userLimit) " +
                "\n   ${super.toString()}"
    }
}

internal class Category(raw: ChannelResponse<IGuildCategory>) : GuildChannel(raw), IGuildCategory {
    override val parentId: Snowflake? = raw.parent_id
}

internal class AnnouncementChannel(raw: ChannelResponse<IAnnouncementChannel>) : GuildChannel(raw),
    IAnnouncementChannel {
    override fun toString(): String {
        return "AnnouncementChannel() " +
                "\n   ${super.toString()}"
    }

    override suspend fun crosspostToFollowers(messageId: Snowflake): IMessage = rest.call {
        channelService.crosspostMessage(id, messageId)
    }
}

internal class PrivateChannel(override val raw: ChannelResponse<IPrivateChannel>) : Channel(raw), IPrivateChannel,
    ICacheable<IPrivateChannel, UnwrapContext.EmptyContext, ChannelResponse<IPrivateChannel>> {
    override val recipient = NonEmptyList.fromListUnsafe(raw.recipients!!.map { it.unwrap() })

    override val owner = raw.owner_id!!.identify<IUser> {
        client.getUser(it)
    }

    override fun toString(): String {
        return "PrivateChannel(recipient=$recipient, owner=$owner)\n    ${super.toString()}"
    }

    override fun fromCache(): IPrivateChannel = cache[id] as IPrivateChannel

    override fun copy(changes: (ChannelResponse<IPrivateChannel>) -> ChannelResponse<IPrivateChannel>) =
        raw.run(changes).unwrap()
}

internal class GroupPrivateChannel(override val raw: ChannelResponse<IGroupPrivateChannel>) : Channel(raw),
    IGroupPrivateChannel,
    ICacheable<IGroupPrivateChannel, UnwrapContext.EmptyContext, ChannelResponse<IGroupPrivateChannel>> {
    override val icon = raw.icon?.let { ImageResponse(it, null) }?.unwrap()

    override fun toString(): String {
        return "GroupPrivateChannel(icon=$icon)\n   ${super.toString()}"
    }

    override val owner: IdentifiedF<ForIO, IUser> = raw.owner_id!!.identify<IUser> { client.getUser(it) }
    override val recipient: NonEmptyList<IUser> = Nel.fromListUnsafe(raw.recipients!!.map { it.unwrap() })

    override fun fromCache(): IGroupPrivateChannel = cache[id] as IGroupPrivateChannel

    override fun copy(changes: (ChannelResponse<IGroupPrivateChannel>) -> ChannelResponse<IGroupPrivateChannel>) =
        raw.run(changes).unwrap()
}
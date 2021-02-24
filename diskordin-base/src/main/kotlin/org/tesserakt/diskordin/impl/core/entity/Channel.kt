package org.tesserakt.diskordin.impl.core.entity

import arrow.core.Nel
import arrow.core.NonEmptyList
import kotlinx.coroutines.flow.map
import mu.KotlinLogging
import org.tesserakt.diskordin.core.data.DeferredIdentified
import org.tesserakt.diskordin.core.data.Permissions
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.deferred
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
import org.tesserakt.diskordin.rest.flow
import org.tesserakt.diskordin.util.enums.and
import org.tesserakt.diskordin.util.enums.not

internal sealed class Channel(raw: ChannelResponse<IChannel>) : IChannel {
    private val logger = KotlinLogging.logger { }

    final override val type: IChannel.Type = IChannel.Type.values().first { it.ordinal == raw.type }

    final override val id: Snowflake = raw.id

    final override val mention: String = "<#$id>"

    final override suspend fun delete(reason: String?) = rest.call {
        channelService.deleteChannel(id, reason)
    }.let { }

    override fun toString(): String {
        return "Channel(type=$type, id=$id, mention='$mention', invites=$invites)"
    }

    override val invites = rest.flow {
        channelService.getChannelInvites(id)
    }
}

internal sealed class GuildChannel(raw: ChannelResponse<IGuildChannel>) : Channel(raw), IGuildChannel {
    final override val position: Int = raw.position!!

    final override val permissionOverwrites = raw.permission_overwrites!!.map { it.unwrap() }

    final override val parentCategory: Snowflake? = raw.parent_id

    final override val guild = raw.guild_id!! deferred {
        client.getGuild(it)
    }

    final override val name: String = raw.name!!

    final override val invites = rest.flow {
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

internal class TextChannel(override val raw: ChannelResponse<ITextChannel>) : GuildChannel(raw), ITextChannel,
    ICacheable<ITextChannel, UnwrapContext.EmptyContext, ChannelResponse<ITextChannel>> {
    override val pins = rest.flow {
        channelService.getPinnedMessages(id)
    }

    override val isNSFW: Boolean = raw.nsfw ?: false

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

    override fun copy(changes: (ChannelResponse<ITextChannel>) -> ChannelResponse<ITextChannel>): ITextChannel =
        raw.run(changes).unwrap()
}

internal class VoiceChannel(override val raw: ChannelResponse<IVoiceChannel>) : GuildChannel(raw), IVoiceChannel,
    ICacheable<IVoiceChannel, UnwrapContext.EmptyContext, ChannelResponse<IVoiceChannel>> {
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

    override fun copy(changes: (ChannelResponse<IVoiceChannel>) -> ChannelResponse<IVoiceChannel>): IVoiceChannel =
        raw.run(changes).unwrap()
}

internal class Category(override val raw: ChannelResponse<IGuildCategory>) : GuildChannel(raw), IGuildCategory,
    ICacheable<IGuildCategory, UnwrapContext.EmptyContext, ChannelResponse<IGuildCategory>> {
    override val parentId: Snowflake? = raw.parent_id

    override fun copy(changes: (ChannelResponse<IGuildCategory>) -> ChannelResponse<IGuildCategory>): IGuildCategory =
        raw.run(changes).unwrap()

    override fun toString(): String {
        return "Category(parentId=$parentId) " +
                "\n   ${super.toString()}"
    }
}

internal class AnnouncementChannel(override val raw: ChannelResponse<IAnnouncementChannel>) : GuildChannel(raw),
    IAnnouncementChannel,
    ICacheable<IAnnouncementChannel, UnwrapContext.EmptyContext, ChannelResponse<IAnnouncementChannel>> {
    override fun toString(): String {
        return "AnnouncementChannel() " +
                "\n   ${super.toString()}"
    }

    override suspend fun crosspostToFollowers(messageId: Snowflake): IMessage = rest.call {
        channelService.crosspostMessage(id, messageId)
    }

    override fun copy(changes: (ChannelResponse<IAnnouncementChannel>) -> ChannelResponse<IAnnouncementChannel>) =
        raw.run(changes).unwrap()
}

internal class PrivateChannel(override val raw: ChannelResponse<IPrivateChannel>) : Channel(raw), IPrivateChannel,
    ICacheable<IPrivateChannel, UnwrapContext.EmptyContext, ChannelResponse<IPrivateChannel>> {
    override val recipient = NonEmptyList.fromListUnsafe(raw.recipients!!.map { it.unwrap() })
    override val owner: DeferredIdentified<IUser> = raw.owner_id?.deferred { client.getUser(it) } ?: client.self

    override fun toString(): String {
        return "PrivateChannel(recipient=$recipient)\n    ${super.toString()}"
    }

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

    override val owner = raw.owner_id?.deferred { client.getUser(it) } ?: client.self
    override val recipient: NonEmptyList<IUser> = Nel.fromListUnsafe(raw.recipients!!.map { it.unwrap() })

    override fun copy(changes: (ChannelResponse<IGroupPrivateChannel>) -> ChannelResponse<IGroupPrivateChannel>) =
        raw.run(changes).unwrap()
}
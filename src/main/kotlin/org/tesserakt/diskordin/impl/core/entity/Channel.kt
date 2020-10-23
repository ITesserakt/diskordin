package org.tesserakt.diskordin.impl.core.entity

import arrow.core.Id
import arrow.core.ListK
import arrow.core.NonEmptyList
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.functor.functor
import arrow.core.extensions.listk.functor.functor
import arrow.core.extensions.listk.functor.map
import arrow.core.fix
import arrow.fx.coroutines.stream.Stream
import arrow.fx.coroutines.stream.callback
import mu.KotlinLogging
import org.tesserakt.diskordin.core.data.Permissions
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.ChannelResponse
import org.tesserakt.diskordin.core.data.json.response.ImageResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import org.tesserakt.diskordin.core.entity.`object`.IPermissionOverwrite
import org.tesserakt.diskordin.core.entity.builder.PermissionEditBuilder
import org.tesserakt.diskordin.core.entity.builder.TextChannelEditBuilder
import org.tesserakt.diskordin.core.entity.builder.VoiceChannelEditBuilder
import org.tesserakt.diskordin.core.entity.builder.instance
import org.tesserakt.diskordin.rest.call
import org.tesserakt.diskordin.util.enums.and
import org.tesserakt.diskordin.util.enums.not

internal sealed class Channel(raw: ChannelResponse<IChannel>) : IChannel {
    private val logger = KotlinLogging.logger { }

    final override val type: IChannel.Type = IChannel.Type.values().first { it.ordinal == raw.type }

    final override val id: Snowflake = raw.id

    final override val mention: String = "<#${id.asString()}>"

    final override suspend fun delete(reason: String?) = rest.call(Id.functor()) {
        channelService.deleteChannel(id, reason)
    }.let { }

    override fun toString(): String {
        return StringBuilder("Channel(")
            .appendLine("type=$type, ")
            .appendLine("id=$id, ")
            .appendLine("mention='$mention', ")
            .appendLine("invites=$invites")
            .appendLine(")")
            .toString()
    }

    override val invites = Stream.callback {
        rest.call(ListK.functor()) {
            channelService.getChannelInvites(id)
        }.fix().forEach { emit(it) }
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

    final override val invites = Stream.callback {
        rest.call(ListK.functor()) {
            channelService.getChannelInvites(id)
        }.map { list -> list as IGuildInvite }.forEach { emit(it) }
    }

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
        return StringBuilder("GuildChannel(")
            .appendLine("position=$position, ")
            .appendLine("permissionOverwrites=$permissionOverwrites, ")
            .appendLine("parentCategory=$parentCategory, ")
            .appendLine("guild=$guild, ")
            .appendLine("name='$name', ")
            .appendLine("invites=$invites")
            .appendLine(") ${super.toString()}")
            .toString()
    }
}

internal class TextChannel(raw: ChannelResponse<ITextChannel>) : GuildChannel(raw), ITextChannel {
    override val pins = Stream.callback {
        rest.call(ListK.functor()) {
            channelService.getPinnedMessages(id)
        }.fix().forEach { emit(it) }
    }

    override val isNSFW: Boolean = raw.nsfw!!

    override val topic: String? = raw.topic

    @ExperimentalUnsignedTypes
    override val rateLimit: UShort = raw.rate_limit_per_user!!.toUShort()

    override suspend fun edit(builder: TextChannelEditBuilder.() -> Unit) = rest.call(Id.functor()) {
        val inst = builder.instance(::TextChannelEditBuilder)
        channelService.editChannel(id, inst.create(), inst.reason)
    }.extract() as ITextChannel

    @ExperimentalUnsignedTypes
    override fun toString(): String {
        return StringBuilder("TextChannel(")
            .appendLine("messages=$cachedMessages, ")
            .appendLine("isNSFW=$isNSFW, ")
            .appendLine("topic=$topic, ")
            .appendLine("rateLimit=$rateLimit")
            .appendLine(") ${super.toString()}")
            .toString()
    }
}

internal class VoiceChannel(raw: ChannelResponse<IVoiceChannel>) : GuildChannel(raw), IVoiceChannel {
    override val bitrate: Int = raw.bitrate!!

    override val userLimit: Int = raw.user_limit!!

    override suspend fun edit(builder: VoiceChannelEditBuilder.() -> Unit) = rest.call(Id.functor()) {
        val inst = builder.instance(::VoiceChannelEditBuilder)
        channelService.editChannel(id, inst.create(), inst.reason)
    }.extract() as IVoiceChannel

    override fun toString(): String {
        return StringBuilder("VoiceChannel(")
            .appendLine("bitrate=$bitrate, ")
            .appendLine("userLimit=$userLimit")
            .appendLine(") ${super.toString()}")
            .toString()
    }
}

internal class Category(raw: ChannelResponse<IGuildCategory>) : GuildChannel(raw), IGuildCategory {
    override val parentId: Snowflake? = raw.parent_id
}

internal class AnnouncementChannel(raw: ChannelResponse<IAnnouncementChannel>) : GuildChannel(raw),
    IAnnouncementChannel {
    override fun toString(): String {
        return StringBuilder("AnnouncementChannel(")
            .appendLine("messages=$cachedMessages")
            .appendLine(") ${super.toString()}")
            .toString()
    }

    override suspend fun crosspostToFollowers(messageId: Snowflake): IMessage = rest.call {
        channelService.crosspostMessage(id, messageId)
    }
}

internal open class PrivateChannel(raw: ChannelResponse<IPrivateChannel>) : Channel(raw), IPrivateChannel {
    override val recipient = NonEmptyList.fromListUnsafe(raw.recipients!!.map { it.unwrap() })

    override fun toString(): String {
        return StringBuilder("PrivateChannel(")
            .appendLine("recipient=$recipient, ")
            .appendLine("owner=$owner, ")
            .appendLine("messages=$cachedMessages")
            .appendLine(") ${super.toString()}")
            .toString()
    }

    override val owner = raw.owner_id?.identify<IUser> {
        client.getUser(it)
    }
}

internal class GroupPrivateChannel(raw: ChannelResponse<IGroupPrivateChannel>) : PrivateChannel(raw),
    IGroupPrivateChannel {
    override val icon = raw.icon?.let { ImageResponse(it, null) }?.unwrap()

    override fun toString(): String {
        return StringBuilder("GroupPrivateChannel(")
            .appendLine("recipient=$recipient, ")
            .appendLine("owner=$owner, ")
            .appendLine("icon=$icon, ")
            .appendLine("messages=$cachedMessages")
            .appendLine(") ${super.toString()}")
            .toString()
    }
}
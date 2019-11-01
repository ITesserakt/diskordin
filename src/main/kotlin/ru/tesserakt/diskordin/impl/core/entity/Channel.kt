package ru.tesserakt.diskordin.impl.core.entity

import arrow.core.NonEmptyList
import kotlinx.coroutines.flow.*
import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.combine
import ru.tesserakt.diskordin.core.data.json.response.ChannelResponse
import ru.tesserakt.diskordin.core.data.json.response.ImageResponse
import ru.tesserakt.diskordin.core.data.json.response.unwrap
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import ru.tesserakt.diskordin.core.entity.`object`.IInvite
import ru.tesserakt.diskordin.core.entity.`object`.IPermissionOverwrite
import ru.tesserakt.diskordin.core.entity.builder.*
import ru.tesserakt.diskordin.core.entity.query.MessagesQuery
import ru.tesserakt.diskordin.util.Loggers
import ru.tesserakt.diskordin.util.enums.not

sealed class Channel(raw: ChannelResponse<IChannel>) : IChannel {
    private val logger by Loggers

    final override val type: IChannel.Type = IChannel.Type.values().first { it.ordinal == raw.type }

    final override val id: Snowflake = raw.id

    final override val mention: String = "<#${id.asString()}>"

    final override suspend fun delete(reason: String?) {
        channelService.deleteChannel<IChannel>(id, reason)
    }

    override suspend fun invite(builder: InviteCreateBuilder.() -> Unit): IInvite =
        channelService.createChannelInvite(id, builder.build(), null).unwrap()

    override fun toString(): String {
        return "Channel(type=$type, id=$id, mention='$mention', invites=$invites)"
    }

    override val invites: Flow<IInvite> = flow {
        channelService.getChannelInvites(id).map { it.unwrap() }.forEach { emit(it) }
    }
}

sealed class GuildChannel(raw: ChannelResponse<IGuildChannel>) : Channel(raw), IGuildChannel {
    init {
        requireNotNull(raw.guild_id)
        requireNotNull(raw.permission_overwrites)
        requireNotNull(raw.position)
        requireNotNull(raw.name)
    }

    final override val position: Int = raw.position!!

    final override val permissionOverwrites: Flow<IPermissionOverwrite> = raw.permission_overwrites!!.map {
        it.unwrap()
    }.asFlow()

    final override val parentCategory: Snowflake? = raw.parent_id

    final override val guild: Identified<IGuild> =
        raw.guild_id!!.combine { client.getGuild(it) }

    final override val name: String = raw.name!!

    final override val invites: Flow<IGuildInvite> = flow {
        channelService.getChannelInvites(id).map { it.unwrap() as IGuildInvite }.forEach { emit(it) }
    }

    final override suspend fun invite(builder: InviteCreateBuilder.() -> Unit): IGuildInvite =
        channelService.createChannelInvite(id, builder.build(), null).unwrap() as IGuildInvite

    final override suspend fun editPermissions(
        overwrite: IPermissionOverwrite,
        builder: PermissionEditBuilder.() -> Unit
    ) =
        channelService.editChannelPermissions(id, (overwrite.allowed and !overwrite.denied).code, builder.build(), null)

    final override suspend fun removePermissions(toRemove: IPermissionOverwrite, reason: String?) =
        channelService.deleteChannelPermissions(id, (toRemove.allowed and !toRemove.denied).code, reason)

    override fun toString(): String {
        return "GuildChannel(position=$position, permissionOverwrites=$permissionOverwrites, parentCategory=$parentCategory, guild=$guild, name='$name', invites=$invites) ${super.toString()}"
    }
}

class TextChannel(raw: ChannelResponse<ITextChannel>) : GuildChannel(raw), ITextChannel {
    override suspend fun getPinnedMessages(): List<IMessage> = messages.filter { it.isPinned }.toList()

    override suspend fun typing() = channelService.triggerTyping(id)

    override suspend fun deleteMessages(builder: BulkDeleteBuilder.() -> Unit) =
        channelService.bulkDeleteMessages(id, builder.build())

    override val messages: Flow<IMessage> = flow {
        channelService.getMessages(id, MessagesQuery().apply {
            limit = 100
        }.create()).map { it.unwrap() }.forEach { emit(it) }
    }

    init {
        requireNotNull(raw.nsfw)
        requireNotNull(raw.rate_limit_per_user)
    }

    override val isNSFW: Boolean = raw.nsfw!!

    override val topic: String? = raw.topic

    @ExperimentalUnsignedTypes
    override val rateLimit: UShort = raw.rate_limit_per_user!!.toUShort()

    override suspend fun edit(builder: TextChannelEditBuilder.() -> Unit) =
        channelService.editChannel<ITextChannel>(id, builder.build(), null).unwrap()

    override fun toString(): String {
        return "TextChannel(messages=$messages, isNSFW=$isNSFW, topic=$topic, rateLimit=$rateLimit) ${super.toString()}"
    }
}

class VoiceChannel(raw: ChannelResponse<IVoiceChannel>) : GuildChannel(raw), IVoiceChannel {
    init {
        requireNotNull(raw.bitrate)
        requireNotNull(raw.user_limit)
    }

    override val bitrate: Int = raw.bitrate!!

    override val userLimit: Int = raw.user_limit!!

    override suspend fun edit(builder: VoiceChannelEditBuilder.() -> Unit) =
        channelService.editChannel<IVoiceChannel>(id, builder.build(), null).unwrap()

    override fun toString(): String {
        return "VoiceChannel(bitrate=$bitrate, userLimit=$userLimit) ${super.toString()}"
    }
}

class Category(raw: ChannelResponse<IGuildCategory>) : GuildChannel(raw), IGuildCategory {
    override val parentId: Snowflake? = raw.parent_id
}

class AnnouncementChannel(raw: ChannelResponse<IAnnouncementChannel>) : GuildChannel(raw), IAnnouncementChannel {
    override val messages: Flow<IMessage> = flow {
        channelService.getMessages(id, MessagesQuery().apply {
            limit = 1000
        }.create()).map { it.unwrap() }.forEach { emit(it) }
    }

    override suspend fun typing() = channelService.triggerTyping(id)

    override suspend fun deleteMessages(builder: BulkDeleteBuilder.() -> Unit) =
        channelService.bulkDeleteMessages(id, builder.build())

    override fun toString(): String {
        return "AnnouncementChannel(messages=$messages) ${super.toString()}"
    }
}

class PrivateChannel(raw: ChannelResponse<IPrivateChannel>) : Channel(raw), IPrivateChannel {
    override val recipient =
        NonEmptyList.fromListUnsafe(raw.recipients!!.map { it.unwrap() })

    override suspend fun typing() = channelService.triggerTyping(id)

    override suspend fun deleteMessages(builder: BulkDeleteBuilder.() -> Unit) =
        channelService.bulkDeleteMessages(id, builder.build())

    override fun toString(): String {
        return "PrivateChannel(recipient=$recipient, owner=$owner, messages=$messages) ${super.toString()}"
    }

    init {
        requireNotNull(raw.owner_id)
        requireNotNull(raw.recipients)
    }

    override val owner: Identified<IUser> = raw.owner_id!!.combine { client.getUser(it) }

    override val messages: Flow<IMessage> = flow {
        channelService.getMessages(id, MessagesQuery().apply {
            limit = 1000
        }.create()).map { it.unwrap() }.forEach { emit(it) }
    }
}

class GroupPrivateChannel(raw: ChannelResponse<IGroupPrivateChannel>) : Channel(raw), IGroupPrivateChannel {
    override val recipient: NonEmptyList<IUser> =
        NonEmptyList.fromListUnsafe(raw.recipients!!.map { it.unwrap() })

    override val owner: Identified<IUser> = raw.owner_id!!.combine { client.getUser(it) }

    override val icon = raw.icon?.let { ImageResponse(it, null) }?.unwrap()

    override val messages: Flow<IMessage> = flow {
        channelService.getMessages(id, MessagesQuery().apply {
            limit = 1000
        }.create()).map { it.unwrap() }.forEach { emit(it) }
    }

    override suspend fun typing() =
        channelService.triggerTyping(id)

    override suspend fun deleteMessages(builder: BulkDeleteBuilder.() -> Unit) =
        channelService.bulkDeleteMessages(id, builder.build())

    override fun toString(): String {
        return "GroupPrivateChannel(recipient=$recipient, owner=$owner, icon=$icon, messages=$messages) ${super.toString()}"
    }
}
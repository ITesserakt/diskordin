package ru.tesserakt.diskordin.impl.core.entity


import kotlinx.coroutines.flow.*
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.ChannelResponse
import ru.tesserakt.diskordin.core.data.json.response.ImageResponse
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import ru.tesserakt.diskordin.core.entity.`object`.IInvite
import ru.tesserakt.diskordin.core.entity.`object`.IPermissionOverwrite
import ru.tesserakt.diskordin.core.entity.builder.*
import ru.tesserakt.diskordin.impl.core.entity.`object`.Image
import ru.tesserakt.diskordin.impl.core.entity.`object`.PermissionOverwrite
import ru.tesserakt.diskordin.impl.core.service.ChannelService
import ru.tesserakt.diskordin.rest.resource.ChannelResource
import ru.tesserakt.diskordin.util.Identified
import ru.tesserakt.diskordin.util.Loggers

sealed class Channel(raw: ChannelResponse) : IChannel {
    private val logger by Loggers

    final override val type: IChannel.Type = IChannel.Type.values().first { it.ordinal == raw.type }

    final override val id: Snowflake = raw.id.asSnowflake()

    final override val mention: String = "<#$id>"

    final override suspend fun delete(reason: String?) {
        ChannelResource.General.deleteChannel(id.asLong(), reason)
    }

    override suspend fun invite(builder: InviteCreateBuilder.() -> Unit): IInvite =
        ChannelService.newChannelInvite(id, builder)

    override val invites: Flow<IInvite>
        get() = flow {
            ChannelService.getChannelInvites<IInvite>(id).forEach { emit(it) }
        }
}

sealed class GuildChannel(raw: ChannelResponse) : Channel(raw), IGuildChannel {
    init {
        requireNotNull(raw.guild_id)
        requireNotNull(raw.permission_overwrites)
        requireNotNull(raw.position)
        requireNotNull(raw.name)
    }

    final override val position: Int = raw.position!!

    final override val permissionOverwrites: Flow<IPermissionOverwrite> = raw.permission_overwrites!!.map {
        PermissionOverwrite(it)
    }.asFlow()

    final override val parentCategory: Snowflake? = raw.parent_id?.asSnowflake()

    final override val guild: Identified<IGuild> = Identified(
        raw.guild_id!!.asSnowflake()
    ) { client.findGuild(it)!! }

    final override val name: String = raw.name!!

    final override val invites: Flow<IGuildInvite>
        get() = flow {
            ChannelService.getChannelInvites<IGuildInvite>(id).forEach { emit(it) }
        }

    final override suspend fun invite(builder: InviteCreateBuilder.() -> Unit): IGuildInvite =
        ChannelService.newChannelInvite(id, builder)

    final override suspend fun editPermissions(
        overwrite: IPermissionOverwrite,
        builder: PermissionEditBuilder.() -> Unit
    ) =
        ChannelService.editChannelPermissions(id, overwrite, builder)

    final override suspend fun removePermissions(toRemove: IPermissionOverwrite, reason: String?) =
        ChannelService.removeChannelPermissions(id, toRemove, reason)
}

class TextChannel(raw: ChannelResponse) : GuildChannel(raw), ITextChannel {
    override suspend fun getPinnedMessages(): List<IMessage> = messages.filter { it.isPinned }.toList()

    override suspend fun typing() = ChannelService.triggerTyping(id)

    override suspend fun createMessage(content: String): IMessage = createMessage {
        this.content = content
    }

    override suspend fun createMessage(builder: MessageCreateBuilder.() -> Unit): IMessage =
        ChannelService.createMessage(id, builder)

    override suspend fun createEmbed(builder: EmbedCreateBuilder.() -> Unit): IMessage =
        createMessage { embed = builder }

    override suspend fun deleteMessages(builder: BulkDeleteBuilder.() -> Unit) =
        ChannelService.bulkDeleteMessages(id, builder)

    override val messages: Flow<IMessage>
        get() = flow {
            ChannelService.getMessages(id) {
                this.limit = 1000
            }.forEach { emit(it) }
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
        ChannelService.editChannel(id, builder)
}

class VoiceChannel(raw: ChannelResponse) : GuildChannel(raw), IVoiceChannel {
    init {
        requireNotNull(raw.bitrate)
        requireNotNull(raw.user_limit)
    }

    override val bitrate: Int = raw.bitrate!!

    override val userLimit: Int = raw.user_limit!!

    override suspend fun edit(builder: VoiceChannelEditBuilder.() -> Unit) =
        ChannelService.editChannel(id, builder)
}

class Category(raw: ChannelResponse) : GuildChannel(raw), IGuildCategory {
    override val parentId: Snowflake? = raw.parent_id?.asSnowflake()
}

class AnnouncementChannel(raw: ChannelResponse) : GuildChannel(raw), IAnnouncementChannel {
    override val messages: Flow<IMessage> = flow {
        ChannelService.getMessages(id) { limit = 1000 }.forEach { emit(it) }
    }

    override suspend fun typing() = ChannelService.triggerTyping(id)

    override suspend fun createMessage(content: String): IMessage = createMessage { this.content = content }

    override suspend fun createMessage(builder: MessageCreateBuilder.() -> Unit): IMessage =
        ChannelService.createMessage(id, builder)

    override suspend fun createEmbed(builder: EmbedCreateBuilder.() -> Unit): IMessage = createMessage {
        embed = builder
    }

    override suspend fun deleteMessages(builder: BulkDeleteBuilder.() -> Unit) =
        ChannelService.bulkDeleteMessages(id, builder)
}

class PrivateChannel(raw: ChannelResponse) : Channel(raw), IPrivateChannel {
    override val recipient: Identified<IUser> = raw.recipients!![0].let { user ->
        Identified(user.id.asSnowflake()) { User(user) }
    }

    override suspend fun typing() = ChannelService.triggerTyping(id)

    override suspend fun createMessage(content: String): IMessage = createMessage {
        this.content = content
    }

    override suspend fun createMessage(builder: MessageCreateBuilder.() -> Unit): IMessage =
        ChannelService.createMessage(id, builder)

    override suspend fun createEmbed(builder: EmbedCreateBuilder.() -> Unit): IMessage =
        createMessage { embed = builder }

    override suspend fun deleteMessages(builder: BulkDeleteBuilder.() -> Unit) =
        ChannelService.bulkDeleteMessages(id, builder)

    init {
        requireNotNull(raw.owner_id)
        requireNotNull(raw.recipients)
    }

    override val owner: Identified<IUser> = Identified(
        raw.owner_id!!.asSnowflake()
    ) { client.findUser(it)!! }

    override val messages: Flow<IMessage>
        get() = flow {
            ChannelService.getMessages(id) {
                this.limit = 1000
            }.forEach { emit(it) }
        }
}

class GroupPrivateChannel(raw: ChannelResponse) : Channel(raw), IGroupPrivateChannel {
    override val owner: Identified<IUser> = Identified(raw.owner_id!!.asSnowflake()) {
        client.findUser(it)!!
    }

    override val recipients: Flow<IUser> = raw.recipients!!
        .map { User(it) }
        .asFlow()

    override val icon: Image? = raw.icon?.let { ImageResponse(it, null) }?.let { Image(it) }

    override val messages: Flow<IMessage> = flow {
        ChannelService.getMessages(id) {
            limit = 1000
        }.forEach { emit(it) }
    }

    override suspend fun typing() =
        ChannelService.triggerTyping(id)


    override suspend fun createMessage(content: String): IMessage = createMessage {
        this.content = content
    }

    override suspend fun createMessage(builder: MessageCreateBuilder.() -> Unit): IMessage =
        ChannelService.createMessage(id, builder)


    override suspend fun createEmbed(builder: EmbedCreateBuilder.() -> Unit): IMessage = createMessage {
        embed = builder
    }

    override suspend fun deleteMessages(builder: BulkDeleteBuilder.() -> Unit) =
        ChannelService.bulkDeleteMessages(id, builder)
}
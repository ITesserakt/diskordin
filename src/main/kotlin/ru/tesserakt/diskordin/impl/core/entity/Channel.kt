package ru.tesserakt.diskordin.impl.core.entity

import arrow.core.Id
import arrow.core.ListK
import arrow.core.NonEmptyList
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.functor.functor
import arrow.core.extensions.listk.functor.functor
import arrow.core.fix
import arrow.fx.extensions.io.applicative.map
import arrow.fx.fix
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
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
import ru.tesserakt.diskordin.rest.call
import ru.tesserakt.diskordin.util.Loggers
import ru.tesserakt.diskordin.util.enums.not
import kotlin.time.ExperimentalTime

sealed class Channel(raw: ChannelResponse<IChannel>) : IChannel {
    private val logger by Loggers

    final override val type: IChannel.Type = IChannel.Type.values().first { it.ordinal == raw.type }

    final override val id: Snowflake = raw.id

    final override val mention: String = "<#${id.asString()}>"

    final override suspend fun delete(reason: String?) = rest.call(Id.functor()) {
        channelService.deleteChannel<IChannel>(id, reason)
    }.map { Unit }.suspended()

    @ExperimentalTime
    override suspend fun invite(builder: InviteCreateBuilder.() -> Unit): IInvite = rest.call(Id.functor()) {
        val inst = builder.instance()
        channelService.createChannelInvite(id, inst.create(), inst.reason)
    }.fix().suspended().extract()

    override fun toString(): String {
        return "Channel(type=$type, id=$id, mention='$mention', invites=$invites)"
    }

    override val invites: Flow<IInvite> = flow {
        rest.call(ListK.functor()) {
            channelService.getChannelInvites(id)
        }.fix().suspended().fix().forEach { emit(it) }
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
        rest.call(ListK.functor()) {
            channelService.getChannelInvites(id)
        }.fix().suspended().fix().forEach { emit(it as IGuildInvite) }
    }

    @ExperimentalTime
    final override suspend fun invite(builder: InviteCreateBuilder.() -> Unit): IGuildInvite = rest.call(Id.functor()) {
        channelService.createChannelInvite(id, builder.build(), null)
    }.fix().suspended().extract() as IGuildInvite

    final override suspend fun editPermissions(
        overwrite: IPermissionOverwrite,
        builder: PermissionEditBuilder.() -> Unit
    ) = rest.effect {
        val inst = builder.instance()
        channelService.editChannelPermissions(
            id,
            (overwrite.allowed and !overwrite.denied).code,
            inst.create(),
            inst.reason
        )
    }.fix().suspended()

    final override suspend fun removePermissions(toRemove: IPermissionOverwrite, reason: String?) = rest.effect {
        channelService.deleteChannelPermissions(id, (toRemove.allowed and !toRemove.denied).code, reason)
    }.fix().suspended()

    override fun toString(): String {
        return "GuildChannel(position=$position, permissionOverwrites=$permissionOverwrites, parentCategory=$parentCategory, guild=$guild, name='$name', invites=$invites) ${super.toString()}"
    }
}

class TextChannel(raw: ChannelResponse<ITextChannel>) : GuildChannel(raw), ITextChannel {
    override suspend fun getPinnedMessages(): List<IMessage> = rest.call(ListK.functor()) {
        channelService.getPinnedMessages(id)
    }.fix().suspended().fix()

    override val messages: Flow<IMessage> = flow {
        rest.call(ListK.functor()) {
            channelService.getMessages(id, MessagesQuery().apply {
                limit = 100
            }.create())
        }.fix().suspended().fix().forEach { emit(it) }
    }

    init {
        requireNotNull(raw.nsfw)
        requireNotNull(raw.rate_limit_per_user)
    }

    override val isNSFW: Boolean = raw.nsfw!!

    override val topic: String? = raw.topic

    @ExperimentalUnsignedTypes
    override val rateLimit: UShort = raw.rate_limit_per_user!!.toUShort()

    override suspend fun edit(builder: TextChannelEditBuilder.() -> Unit) = rest.call(Id.functor()) {
        val inst = builder.instance()
        channelService.editChannel<ITextChannel>(id, inst.create(), inst.reason)
    }.fix().suspended().extract()

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

    override suspend fun edit(builder: VoiceChannelEditBuilder.() -> Unit) = rest.call(Id.functor()) {
        val inst = builder.instance()
        channelService.editChannel<IVoiceChannel>(id, inst.create(), inst.reason)
    }.fix().suspended().extract()

    override fun toString(): String {
        return "VoiceChannel(bitrate=$bitrate, userLimit=$userLimit) ${super.toString()}"
    }
}

class Category(raw: ChannelResponse<IGuildCategory>) : GuildChannel(raw), IGuildCategory {
    override val parentId: Snowflake? = raw.parent_id
}

class AnnouncementChannel(raw: ChannelResponse<IAnnouncementChannel>) : GuildChannel(raw), IAnnouncementChannel {
    override val messages: Flow<IMessage> = flow {
        rest.call(ListK.functor()) {
            channelService.getMessages(id, MessagesQuery().apply {
                limit = 1000
            }.create())
        }.fix().suspended().fix().forEach { emit(it) }
    }

    override fun toString(): String {
        return "AnnouncementChannel(messages=$messages) ${super.toString()}"
    }
}

class PrivateChannel(raw: ChannelResponse<IPrivateChannel>) : Channel(raw), IPrivateChannel {
    override val recipient = NonEmptyList.fromListUnsafe(raw.recipients!!.map { it.unwrap() })

    override fun toString(): String {
        return "PrivateChannel(recipient=$recipient, owner=$owner, messages=$messages) ${super.toString()}"
    }

    init {
        requireNotNull(raw.owner_id)
        requireNotNull(raw.recipients)
    }

    override val owner: Identified<IUser> = raw.owner_id!!.combine { client.getUser(it) }

    override val messages: Flow<IMessage> = flow {
        rest.call(ListK.functor()) {
            channelService.getMessages(id, MessagesQuery().apply {
                limit = 1000
            }.create())
        }.fix().suspended().fix().forEach { emit(it) }
    }
}

class GroupPrivateChannel(raw: ChannelResponse<IGroupPrivateChannel>) : Channel(raw), IGroupPrivateChannel {
    override val recipient: NonEmptyList<IUser> =
        NonEmptyList.fromListUnsafe(raw.recipients!!.map { it.unwrap() })

    override val owner: Identified<IUser> = raw.owner_id!!.combine { client.getUser(it) }

    override val icon = raw.icon?.let { ImageResponse(it, null) }?.unwrap()

    override val messages: Flow<IMessage> = flow {
        rest.call(ListK.functor()) {
            channelService.getMessages(id, MessagesQuery().apply {
                limit = 1000
            }.create())
        }.fix().suspended().fix().forEach { emit(it) }
    }

    override fun toString(): String {
        return "GroupPrivateChannel(recipient=$recipient, owner=$owner, icon=$icon, messages=$messages) ${super.toString()}"
    }
}
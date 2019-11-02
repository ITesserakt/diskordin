package ru.tesserakt.diskordin.impl.core.entity

import arrow.core.*
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.functor.functor
import arrow.core.extensions.listk.functor.functor
import arrow.core.extensions.listk.functor.map
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.just
import arrow.fx.extensions.io.applicative.map
import arrow.fx.fix
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.identify
import ru.tesserakt.diskordin.core.data.json.response.ChannelResponse
import ru.tesserakt.diskordin.core.data.json.response.ImageResponse
import ru.tesserakt.diskordin.core.data.json.response.unwrap
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import ru.tesserakt.diskordin.core.entity.`object`.IInvite
import ru.tesserakt.diskordin.core.entity.`object`.IPermissionOverwrite
import ru.tesserakt.diskordin.core.entity.builder.*
import ru.tesserakt.diskordin.rest.call
import ru.tesserakt.diskordin.util.Loggers
import ru.tesserakt.diskordin.util.enums.not
import kotlin.time.ExperimentalTime

sealed class Channel(raw: ChannelResponse<IChannel>) : IChannel {
    private val logger by Loggers

    final override val type: IChannel.Type = IChannel.Type.values().first { it.ordinal == raw.type }

    final override val id: Snowflake = raw.id

    final override val mention: String = "<#${id.asString()}>"

    final override fun delete(reason: String?) = rest.call(Id.functor()) {
        channelService.deleteChannel(id, reason)
    }.map { Unit }

    @ExperimentalTime
    override fun invite(builder: InviteCreateBuilder.() -> Unit): IO<IInvite> = rest.call(Id.functor()) {
        val inst = builder.instance()
        channelService.createChannelInvite(id, inst.create(), inst.reason)
    }.map { it.extract() }

    override fun toString(): String {
        return "Channel(type=$type, id=$id, mention='$mention', invites=$invites)"
    }

    override val invites: IO<ListK<IInvite>> = rest.call(ListK.functor()) {
        channelService.getChannelInvites(id)
    }.map { it.fix() }
}

sealed class GuildChannel(raw: ChannelResponse<IGuildChannel>) : Channel(raw), IGuildChannel {
    final override val position: Int = raw.position!!

    final override val permissionOverwrites = raw.permission_overwrites!!.map {
        it.unwrap()
    }.k().just()

    final override val parentCategory: Snowflake? = raw.parent_id

    final override val guild = raw.guild_id!!.identify {
        client.getGuild(it).bind()
    }

    final override val name: String = raw.name!!

    final override val invites: IO<ListK<IGuildInvite>> = rest.call(ListK.functor()) {
        channelService.getChannelInvites(id)
    }.map { list -> list.map { it as IGuildInvite } }

    @ExperimentalTime
    final override fun invite(builder: InviteCreateBuilder.() -> Unit): IO<IGuildInvite> = rest.call(Id.functor()) {
        channelService.createChannelInvite(id, builder.build(), null)
    }.map { it.extract() as IGuildInvite }

    final override fun editPermissions(overwrite: IPermissionOverwrite, builder: PermissionEditBuilder.() -> Unit) =
        rest.effect {
            val inst = builder.instance()
            channelService.editChannelPermissions(
                id,
                (overwrite.allowed and !overwrite.denied).code,
                inst.create(),
                inst.reason
            )
        }.fix()

    final override fun removePermissions(toRemove: IPermissionOverwrite, reason: String?) = rest.effect {
        channelService.deleteChannelPermissions(id, (toRemove.allowed and !toRemove.denied).code, reason)
    }.fix()

    override fun toString(): String {
        return "GuildChannel(position=$position, permissionOverwrites=$permissionOverwrites, parentCategory=$parentCategory, guild=$guild, name='$name', invites=$invites) ${super.toString()}"
    }
}

class TextChannel(raw: ChannelResponse<ITextChannel>) : GuildChannel(raw), ITextChannel {
    override fun getPinnedMessages(): IO<ListK<IMessage>> = rest.call(ListK.functor()) {
        channelService.getPinnedMessages(id)
    }.map { it.fix() }

    override val isNSFW: Boolean = raw.nsfw!!

    override val topic: String? = raw.topic

    @ExperimentalUnsignedTypes
    override val rateLimit: UShort = raw.rate_limit_per_user!!.toUShort()

    override fun edit(builder: TextChannelEditBuilder.() -> Unit): IO<ITextChannel> = rest.call(Id.functor()) {
        val inst = builder.instance()
        channelService.editChannel(id, inst.create(), inst.reason)
    }.map { it.extract() as ITextChannel }

    @ExperimentalUnsignedTypes
    override fun toString(): String {
        return "TextChannel(messages=$messages, isNSFW=$isNSFW, topic=$topic, rateLimit=$rateLimit) ${super.toString()}"
    }
}

class VoiceChannel(raw: ChannelResponse<IVoiceChannel>) : GuildChannel(raw), IVoiceChannel {
    override val bitrate: Int = raw.bitrate!!

    override val userLimit: Int = raw.user_limit!!

    override fun edit(builder: VoiceChannelEditBuilder.() -> Unit) = rest.call(Id.functor()) {
        val inst = builder.instance()
        channelService.editChannel(id, inst.create(), inst.reason)
    }.map { it.extract() as IVoiceChannel }

    override fun toString(): String {
        return "VoiceChannel(bitrate=$bitrate, userLimit=$userLimit) ${super.toString()}"
    }
}

class Category(raw: ChannelResponse<IGuildCategory>) : GuildChannel(raw), IGuildCategory {
    override val parentId: Snowflake? = raw.parent_id
}

class AnnouncementChannel(raw: ChannelResponse<IAnnouncementChannel>) : GuildChannel(raw), IAnnouncementChannel {
    override fun toString(): String {
        return "AnnouncementChannel(messages=$messages) ${super.toString()}"
    }
}

open class PrivateChannel(raw: ChannelResponse<IPrivateChannel>) : Channel(raw), IPrivateChannel {
    override val recipient = NonEmptyList.fromListUnsafe(raw.recipients!!.map { it.unwrap() })

    override fun toString(): String {
        return "PrivateChannel(recipient=$recipient, owner=$owner, messages=$messages) ${super.toString()}"
    }

    override val owner = raw.owner_id!!.identify {
        client.getUser(it).bind()
    }
}

class GroupPrivateChannel(raw: ChannelResponse<IGroupPrivateChannel>) : PrivateChannel(raw), IGroupPrivateChannel {
    override val icon = raw.icon?.let { ImageResponse(it, null) }?.unwrap()

    override fun toString(): String {
        return "GroupPrivateChannel(recipient=$recipient, owner=$owner, icon=$icon, messages=$messages) ${super.toString()}"
    }
}
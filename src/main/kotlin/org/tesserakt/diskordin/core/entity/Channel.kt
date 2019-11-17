@file:JvmMultifileClass
@file:Suppress("UNUSED", "UNUSED_PARAMETER")

package org.tesserakt.diskordin.core.entity

import arrow.core.Id
import arrow.core.ListK
import arrow.core.NonEmptyList
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.functor.functor
import arrow.core.extensions.listk.functor.functor
import arrow.core.fix
import arrow.fx.IO
import arrow.fx.extensions.io.monad.map
import arrow.fx.fix
import org.tesserakt.diskordin.core.data.Identified
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.ChannelResponse
import org.tesserakt.diskordin.core.entity.IChannel.Type.*
import org.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import org.tesserakt.diskordin.core.entity.`object`.IImage
import org.tesserakt.diskordin.core.entity.`object`.IInvite
import org.tesserakt.diskordin.core.entity.`object`.IPermissionOverwrite
import org.tesserakt.diskordin.core.entity.builder.*
import org.tesserakt.diskordin.core.entity.query.MessagesQuery
import org.tesserakt.diskordin.impl.core.entity.*
import org.tesserakt.diskordin.rest.call

interface IChannel : IMentioned, IDeletable {
    val type: Type
    val invites: IO<ListK<IInvite>>

    enum class Type {
        GuildText,
        Private,
        GuildVoice,
        PrivateGroup,
        GuildCategory,
        GuildNews;
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        internal fun <T : IChannel> typed(response: ChannelResponse<T>) = when (response.type) {
            GuildText.ordinal -> TextChannel(response as ChannelResponse<ITextChannel>)
            Private.ordinal -> PrivateChannel(response as ChannelResponse<IPrivateChannel>)
            GuildVoice.ordinal -> VoiceChannel(response as ChannelResponse<IVoiceChannel>)
            GuildCategory.ordinal -> Category(response as ChannelResponse<IGuildCategory>)
            PrivateGroup.ordinal -> GroupPrivateChannel(response as ChannelResponse<IGroupPrivateChannel>)
            GuildNews.ordinal -> AnnouncementChannel(response as ChannelResponse<IAnnouncementChannel>)
            else -> throw IllegalAccessException("Type of channel isn`t right (!in [0; 6) range)")
        } as T
    }

    fun invite(builder: InviteCreateBuilder.() -> Unit): IO<IInvite>
}

interface IGuildChannel : IChannel, IGuildObject, INamed {
    val position: Int
    val permissionOverwrites: IO<ListK<IPermissionOverwrite>>
    val parentCategory: Snowflake?
    override val invites: IO<ListK<IGuildInvite>>

    override fun invite(builder: InviteCreateBuilder.() -> Unit): IO<IGuildInvite>
    fun removePermissions(toRemove: IPermissionOverwrite, reason: String?): IO<Unit>
    fun editPermissions(overwrite: IPermissionOverwrite, builder: PermissionEditBuilder.() -> Unit): IO<Unit>
}

interface IVoiceChannel : IGuildChannel, IAudioChannel,
    IEditable<IVoiceChannel, VoiceChannelEditBuilder> {
    val bitrate: Int
    val userLimit: Int
}

interface ITextChannel : IGuildChannel, IMessageChannel,
    IEditable<ITextChannel, TextChannelEditBuilder> {
    val isNSFW: Boolean
    val topic: String?

    @ExperimentalUnsignedTypes
    val rateLimit: UShort

    fun getPinnedMessages(): IO<ListK<IMessage>>
}

interface IGuildCategory : IGuildChannel {
    val parentId: Snowflake?
}

interface IAnnouncementChannel : IGuildChannel, IMessageChannel

interface IPrivateChannel : IMessageChannel, IAudioChannel {
    val owner: Identified<IUser>
    val recipient: NonEmptyList<IUser>
}

interface IGroupPrivateChannel : IMessageChannel, IAudioChannel, IPrivateChannel {
    val icon: IImage?
}

interface IMessageChannel : IChannel {
    val messages
        get() = rest.call(ListK.functor()) {
            channelService.getMessages(id, MessagesQuery().apply {
                limit = 100
            }.create())
        }.map { it.fix() }

    fun typing() = rest.effect {
        channelService.triggerTyping(id)
    }.fix()

    fun createMessage(content: String) = createMessage {
        this.content = content
    }

    fun createMessage(builder: MessageCreateBuilder.() -> Unit): IO<IMessage> = rest.call(Id.functor()) {
        channelService.createMessage(id, builder.build())
    }.map { it.extract() }

    fun createEmbed(builder: EmbedCreateBuilder.() -> Unit) = createMessage {
        embed = builder
    }

    fun deleteMessages(builder: BulkDeleteBuilder.() -> Unit) = rest.effect {
        channelService.bulkDeleteMessages(id, builder.build())
    }.fix()
}

interface IAudioChannel : IChannel
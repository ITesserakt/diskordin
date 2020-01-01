@file:JvmMultifileClass
@file:Suppress("UNUSED", "UNUSED_PARAMETER")

package org.tesserakt.diskordin.core.entity

import arrow.core.*
import arrow.core.extensions.listk.functor.functor
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.just
import arrow.fx.extensions.io.functor.map
import arrow.fx.extensions.io.monad.flatTap
import arrow.fx.fix
import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.data.Permissions
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.ChannelResponse
import org.tesserakt.diskordin.core.entity.IChannel.Type.*
import org.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import org.tesserakt.diskordin.core.entity.`object`.IImage
import org.tesserakt.diskordin.core.entity.`object`.IInvite
import org.tesserakt.diskordin.core.entity.`object`.IPermissionOverwrite
import org.tesserakt.diskordin.core.entity.builder.*
import org.tesserakt.diskordin.core.entity.query.MessagesQuery
import org.tesserakt.diskordin.core.entity.query.query
import org.tesserakt.diskordin.impl.core.entity.*
import org.tesserakt.diskordin.rest.call
import kotlin.time.ExperimentalTime

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

    @ExperimentalTime
    fun invite(builder: InviteCreateBuilder.() -> Unit): IO<IInvite> = rest.call {
        val inst = builder.instance()
        channelService.createChannelInvite(id, inst.create(), inst.reason)
    }.fix()
}

interface IGuildChannel : IChannel, IGuildObject<ForIO>, INamed {
    val position: Int
    val permissionOverwrites: List<IPermissionOverwrite>
    val parentCategory: Snowflake?
    override val invites: IO<ListK<IGuildInvite>>

    fun removePermissions(toRemove: IPermissionOverwrite, reason: String?): IO<Unit>
    fun editPermissions(
        overwrite: IPermissionOverwrite,
        type: IPermissionOverwrite.Type,
        allowed: Permissions,
        denied: Permissions,
        reason: String? = null
    ): IO<Unit>
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
    val owner: IdentifiedF<ForIO, IUser>?
    val recipient: NonEmptyList<IUser>
}

interface IGroupPrivateChannel : IMessageChannel, IAudioChannel, IPrivateChannel {
    val icon: IImage?
}

interface IMessageChannel : IChannel {
    val cachedMessages
        get() = cache.values.filterIsInstance<IMessage>()

    fun typing() = rest.effect {
        channelService.triggerTyping(id)
    }.fix()

    fun getMessages(query: MessagesQuery.() -> Unit) = rest.call(ListK.functor()) {
        channelService.getMessages(id, query.query())
    }.map { it.fix() }.flatTap { list -> cache += list.associateBy { it.id }; just() }

    fun getMessage(messageId: Snowflake) = client.getMessage(id, messageId)

    fun createMessage(content: String) = createMessage(content.leftIor())

    fun createMessage(required: Ior<Content, Embed>, builder: MessageCreateBuilder.() -> Unit = {}): IO<IMessage> =
        rest.call {
            channelService.createMessage(id, MessageCreateBuilder(required).apply(builder).create())
        }.fix()

    fun createEmbed(builder: EmbedCreateBuilder.() -> Unit) =
        createMessage(EmbedCreateBuilder().apply(builder).rightIor())

    fun createMessage(
        content: String,
        embed: EmbedCreateBuilder.() -> Unit,
        builder: MessageCreateBuilder.() -> Unit = {}
    ) =
        createMessage((content toT EmbedCreateBuilder().apply(embed)).bothIor(), builder)

    fun deleteMessages(builder: BulkDeleteBuilder.() -> Unit) = rest.effect {
        channelService.bulkDeleteMessages(id, builder.build())
    }.fix()
}

interface IAudioChannel : IChannel
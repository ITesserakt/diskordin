@file:JvmMultifileClass
@file:Suppress("UNUSED", "UNUSED_PARAMETER")

package org.tesserakt.diskordin.core.entity

import arrow.core.*
import arrow.core.extensions.listk.functor.functor
import arrow.fx.ForIO
import arrow.fx.coroutines.stream.Stream
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
    val invites: Stream<IInvite>

    enum class Type {
        GuildText,
        Private,
        GuildVoice,
        PrivateGroup,
        GuildCategory,
        GuildNews;
    }

    @Suppress("UNCHECKED_CAST")
    companion object : StaticMention<IChannel, Companion> {
        internal fun <T : IChannel> typed(response: ChannelResponse<T>) = when (response.type) {
            GuildText.ordinal -> TextChannel(response as ChannelResponse<ITextChannel>)
            Private.ordinal -> PrivateChannel(response as ChannelResponse<IPrivateChannel>)
            GuildVoice.ordinal -> VoiceChannel(response as ChannelResponse<IVoiceChannel>)
            GuildCategory.ordinal -> Category(response as ChannelResponse<IGuildCategory>)
            PrivateGroup.ordinal -> GroupPrivateChannel(response as ChannelResponse<IGroupPrivateChannel>)
            GuildNews.ordinal -> AnnouncementChannel(response as ChannelResponse<IAnnouncementChannel>)
            else -> throw IllegalAccessException("Type of channel isn`t right (!in [0; 6) range)")
        } as T

        override val mention = Regex(""""<#(\d{18,})>"""")
    }

    @ExperimentalTime
    suspend fun invite(builder: InviteCreateBuilder.() -> Unit): IInvite = rest.call {
        val inst = builder.instance(::InviteCreateBuilder)
        channelService.createChannelInvite(id, inst.create(), inst.reason)
    }
}

interface IGuildChannel : IChannel, IGuildObject, INamed {
    val position: Int
    val permissionOverwrites: List<IPermissionOverwrite>
    val parentCategory: Snowflake?
    override val invites: Stream<IGuildInvite>

    suspend fun removePermissions(toRemove: IPermissionOverwrite, reason: String?)
    suspend fun editPermissions(
        overwrite: IPermissionOverwrite,
        type: IPermissionOverwrite.Type,
        allowed: Permissions,
        denied: Permissions,
        reason: String? = null
    )
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
    val pins: Stream<IMessage>
}

interface IGuildCategory : IGuildChannel {
    val parentId: Snowflake?
}

interface IAnnouncementChannel : IGuildChannel, IMessageChannel {
    suspend fun crosspostToFollowers(message: IMessage) = crosspostToFollowers(message.id)
    suspend fun crosspostToFollowers(messageId: Snowflake): IMessage
}

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

    suspend fun typing() = rest.effect {
        channelService.triggerTyping(id)
    }

    suspend fun getMessages(query: MessagesQuery.() -> Unit) = rest.call(ListK.functor()) {
        channelService.getMessages(id, query.query(::MessagesQuery))
    }.fix().also { list -> cache += list.associateBy { it.id } }

    suspend fun getMessage(messageId: Snowflake) = client.getMessage(id, messageId)

    suspend fun createMessage(content: String) = createMessage(content.leftIor())

    suspend fun createMessage(required: Ior<Content, Embed>, builder: MessageCreateBuilder.() -> Unit = {}): IMessage =
        rest.call {
            channelService.createMessage(id, MessageCreateBuilder(required).apply(builder).create())
        }

    suspend fun createEmbed(builder: EmbedCreateBuilder.() -> Unit) =
        createMessage(EmbedCreateBuilder().apply(builder).rightIor())

    suspend fun createMessage(
        content: String,
        embed: EmbedCreateBuilder.() -> Unit,
        builder: MessageCreateBuilder.() -> Unit = {}
    ) = createMessage((content toT EmbedCreateBuilder().apply(embed)).bothIor(), builder)

    suspend fun deleteMessages(builder: BulkDeleteBuilder.() -> Unit) = rest.effect {
        channelService.bulkDeleteMessages(id, builder.build(::BulkDeleteBuilder))
    }
}

interface IAudioChannel : IChannel
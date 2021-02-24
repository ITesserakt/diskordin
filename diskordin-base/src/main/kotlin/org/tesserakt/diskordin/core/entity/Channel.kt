@file:JvmMultifileClass
@file:Suppress("UNUSED", "UNUSED_PARAMETER")

package org.tesserakt.diskordin.core.entity

import arrow.core.*
import kotlinx.coroutines.flow.Flow
import org.tesserakt.diskordin.core.cache.CacheProcessor
import org.tesserakt.diskordin.core.data.DeferredIdentified
import org.tesserakt.diskordin.core.data.Permissions
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import org.tesserakt.diskordin.core.entity.`object`.IImage
import org.tesserakt.diskordin.core.entity.`object`.IInvite
import org.tesserakt.diskordin.core.entity.`object`.IPermissionOverwrite
import org.tesserakt.diskordin.core.entity.builder.*
import org.tesserakt.diskordin.core.entity.query.MessagesQuery
import org.tesserakt.diskordin.core.entity.query.query
import kotlin.time.ExperimentalTime

interface IChannel : IMentioned, IDeletable {
    val type: Type
    val invites: Flow<IInvite>

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
    override val invites: Flow<IGuildInvite>

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
    val pins: Flow<IMessage>
}

interface IGuildCategory : IGuildChannel {
    val parentId: Snowflake?
}

interface IAnnouncementChannel : IGuildChannel, IMessageChannel {
    suspend fun crosspostToFollowers(message: IMessage) = crosspostToFollowers(message.id)
    suspend fun crosspostToFollowers(messageId: Snowflake): IMessage
}

interface IPrivateChannel : IMessageChannel, IAudioChannel {
    val owner: DeferredIdentified<IUser>
    val recipient: NonEmptyList<IUser>
}

interface IGroupPrivateChannel : IMessageChannel, IAudioChannel, IPrivateChannel {
    val icon: IImage?
}

interface IMessageChannel : IChannel {
    val cachedMessages get() = cacheSnapshot.messages.values.filter { it.channel.id == id }

    suspend fun typing() = rest.effect {
        channelService.triggerTyping(id)
    }

    suspend fun getMessages(query: MessagesQuery.() -> Unit) = rest.callRaw {
        channelService.getMessages(id, query.query(::MessagesQuery))
    }.map { it.unwrap() }.onEach { client.context[CacheProcessor].updateData(it) }

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
    ) = createMessage((content to EmbedCreateBuilder().apply(embed)).bothIor(), builder)

    suspend fun deleteMessages(builder: BulkDeleteBuilder.() -> Unit) = rest.effect {
        channelService.bulkDeleteMessages(id, builder.build(::BulkDeleteBuilder))
    }
}

interface IAudioChannel : IChannel
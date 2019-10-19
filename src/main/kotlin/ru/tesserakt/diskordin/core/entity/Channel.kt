@file:JvmMultifileClass
@file:Suppress("UNUSED", "UNUSED_PARAMETER")

package ru.tesserakt.diskordin.core.entity

import arrow.core.NonEmptyList
import kotlinx.coroutines.flow.Flow
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.response.ChannelResponse
import ru.tesserakt.diskordin.core.entity.IChannel.Type.*
import ru.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import ru.tesserakt.diskordin.core.entity.`object`.IImage
import ru.tesserakt.diskordin.core.entity.`object`.IInvite
import ru.tesserakt.diskordin.core.entity.`object`.IPermissionOverwrite
import ru.tesserakt.diskordin.core.entity.builder.*
import ru.tesserakt.diskordin.impl.core.entity.*
import ru.tesserakt.diskordin.util.Identified

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

    suspend fun invite(builder: InviteCreateBuilder.() -> Unit): IInvite
}

interface IGuildChannel : IChannel, IGuildObject, INamed {
    val position: Int
    val permissionOverwrites: Flow<IPermissionOverwrite>
    val parentCategory: Snowflake?
    override val invites: Flow<IGuildInvite>

    override suspend fun invite(builder: InviteCreateBuilder.() -> Unit): IGuildInvite
    suspend fun removePermissions(toRemove: IPermissionOverwrite, reason: String?)
    suspend fun editPermissions(overwrite: IPermissionOverwrite, builder: PermissionEditBuilder.() -> Unit)
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

    suspend fun getPinnedMessages(): List<IMessage>
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
    val messages: Flow<IMessage>

    suspend fun typing()
    suspend fun createMessage(content: String): IMessage
    suspend fun createMessage(builder: MessageCreateBuilder.() -> Unit): IMessage
    suspend fun createEmbed(builder: EmbedCreateBuilder.() -> Unit): IMessage
    suspend fun deleteMessages(builder: BulkDeleteBuilder.() -> Unit)
}

interface IAudioChannel : IChannel
@file:JvmMultifileClass
@file:Suppress("UNUSED", "UNUSED_PARAMETER")

package ru.tesserakt.diskordin.core.entity

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.response.ChannelResponse
import ru.tesserakt.diskordin.core.entity.IChannel.Type.*
import ru.tesserakt.diskordin.core.entity.`object`.IGuildInvite
import ru.tesserakt.diskordin.core.entity.`object`.IInvite
import ru.tesserakt.diskordin.core.entity.`object`.IPermissionOverwrite
import ru.tesserakt.diskordin.core.entity.builder.*
import ru.tesserakt.diskordin.impl.core.entity.PrivateChannel
import ru.tesserakt.diskordin.impl.core.entity.TextChannel
import ru.tesserakt.diskordin.impl.core.entity.VoiceChannel
import ru.tesserakt.diskordin.util.Identified

interface IChannel : IMentioned, IDeletable {
    val type: Type
    @ExperimentalCoroutinesApi
    val invites: Flow<IInvite>

    enum class Type(value: Int) {
        GuildText(0),
        Private(1),
        GuildVoice(2),
        PrivateGroup(3),
        GuildCategory(4),
        GuildNews(5),
        GuildStore(6);

        companion object {
            fun of(value: Int) = when (value) {
                0 -> GuildText
                1 -> Private
                2 -> GuildVoice
                3 -> PrivateGroup
                4 -> GuildCategory
                5 -> GuildNews
                6 -> GuildStore
                else -> throw NoSuchElementException()
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun <T : IChannel> typed(response: ChannelResponse) = when (Type.of(response.type)) {
            GuildText -> TextChannel(response)
            Private -> PrivateChannel(response)
            GuildVoice -> VoiceChannel(response)
            PrivateGroup, GuildCategory, GuildNews, GuildStore -> TODO()
        } as T
    }

    suspend fun invite(builder: InviteCreateBuilder.() -> Unit): IInvite
}

interface IGuildChannel : IChannel, IGuildObject, INamed {
    val position: Int
    @ExperimentalCoroutinesApi
    val permissionOverwrites: Flow<IPermissionOverwrite>
    val parentCategory: Snowflake
    @ExperimentalCoroutinesApi
    override val invites: Flow<IGuildInvite>

    override suspend fun invite(builder: InviteCreateBuilder.() -> Unit): IGuildInvite
    suspend fun removePermissions(toRemove: IPermissionOverwrite, reason: String?)
    suspend fun editPermissions(overwrite: IPermissionOverwrite, builder: PermissionEditBuilder.() -> Unit)
}

interface IVoiceChannel : IGuildChannel, IAudioChannel,
    IEditable<IVoiceChannel, GuildChannelEditBuilder<IVoiceChannel>> {
    val bitrate: Int
    val userLimit: Int
}

interface ITextChannel : IGuildChannel, IMessageChannel,
    IEditable<ITextChannel, GuildChannelEditBuilder<ITextChannel>> {
    val isNSFW: Boolean
    val topic: String?

    @ExperimentalUnsignedTypes
    val rateLimit: UShort

    suspend fun getPinnedMessages(): List<IMessage>
}

interface IPrivateChannel : IMessageChannel, IAudioChannel {
    val owner: Identified<IUser>
    @ExperimentalCoroutinesApi
    val recipients: Flow<IUser>
}

interface IMessageChannel : IChannel {
    @ExperimentalCoroutinesApi
    val messages: Flow<IMessage>

    suspend fun typing()
    suspend fun createMessage(content: String): IMessage
    suspend fun createMessage(builder: MessageCreateBuilder.() -> Unit): IMessage
    suspend fun deleteMessages(builder: BulkDeleteBuilder.() -> Unit)
}
interface IAudioChannel : IChannel
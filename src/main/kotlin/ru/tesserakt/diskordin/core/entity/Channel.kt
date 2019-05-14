@file:JvmMultifileClass
@file:Suppress("UNUSED", "UNUSED_PARAMETER")

package ru.tesserakt.diskordin.core.entity

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.`object`.IPermissionOverwrite
import ru.tesserakt.diskordin.util.Identified

interface IChannel : IMentioned, IDeletable {
    val type: Type

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
}

interface IGuildChannel : IChannel, IGuildObject, INamed {
    val position: Int
    @FlowPreview
    val permissionOverwrites: Flow<IPermissionOverwrite>

    val parentCategory: Snowflake
}

interface IVoiceChannel : IGuildChannel, IAudioChannel {
    val bitrate: Int
    val userLimit: Int
}

interface ITextChannel : IGuildChannel, IMessageChannel {
    val isNSFW: Boolean
    val topic: String?

    @ExperimentalUnsignedTypes
    val rateLimit: UShort
}

interface IPrivateChannel : IMessageChannel, IAudioChannel {

    val owner: Identified<IUser>
    @FlowPreview
    val recipients: Flow<IUser>
}

interface IMessageChannel : IChannel
interface IAudioChannel : IChannel
package ru.tesserakt.diskordin.impl.core.entity

import arrow.core.getOrElse
import arrow.core.handleError
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.ChannelResponse
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.`object`.IPermissionOverwrite
import ru.tesserakt.diskordin.core.rest.service.ChannelService
import ru.tesserakt.diskordin.impl.core.entity.`object`.PermissionOverwrite
import ru.tesserakt.diskordin.util.Identified
import ru.tesserakt.diskordin.util.Loggers

open class Channel(raw: ChannelResponse, override val kodein: Kodein) : IChannel {
    private val logger by Loggers

    final override val type: IChannel.Type = IChannel.Type.of(raw.type)


    final override val id: Snowflake = raw.id.asSnowflake()

    final override val client: IDiscordClient by instance()


    final override val mention: String = "<#$id>"


    final override suspend fun delete(reason: String?) {
        ChannelService.General
            .deleteChannel(id.asLong(), reason)
            .handleError { throw it }
    }
}

open class GuildChannel(raw: ChannelResponse, override val kodein: Kodein) : Channel(raw, kodein), IGuildChannel {
    final override val position: Int = raw.position ?: throw NotGuildChannelException()

    @FlowPreview
    final override val permissionOverwrites: Flow<IPermissionOverwrite> = raw.permission_overwrites?.map {
        PermissionOverwrite(it, kodein)
    }?.asFlow() ?: throw NotGuildChannelException()


    final override val parentCategory: Snowflake = raw.parent_id?.asSnowflake() ?: throw NotGuildChannelException()


    final override val guild: Identified<IGuild> = Identified(
        raw.guild_id?.asSnowflake() ?: throw NotGuildChannelException()
    ) {
        client.findGuild(it).getOrElse { throw NotGuildChannelException() }
    }

    final override val name: String = raw.name ?: throw NotGuildChannelException()
}

class TextChannel(raw: ChannelResponse, override val kodein: Kodein) : GuildChannel(raw, kodein), ITextChannel {
    override val isNSFW: Boolean = raw.nsfw ?: throw NotGuildChannelException()

    override val topic: String? = raw.topic


    @ExperimentalUnsignedTypes
    override val rateLimit: UShort = raw.rate_limit_per_user?.toUShort() ?: throw NotGuildChannelException()
}

class VoiceChannel(raw: ChannelResponse, override val kodein: Kodein) : GuildChannel(raw, kodein), IVoiceChannel {
    override val bitrate: Int = raw.bitrate ?: throw NotGuildChannelException()

    override val userLimit: Int = raw.user_limit ?: throw NotGuildChannelException()
}

class PrivateChannel(raw: ChannelResponse, override val kodein: Kodein) : Channel(raw, kodein), IPrivateChannel {

    override val owner: Identified<IUser> = Identified(
        raw.owner_id?.asSnowflake() ?: throw NotPrivateChannelException()
    ) {
        client.findUser(it).getOrElse { throw NotPrivateChannelException() }
    }

    @FlowPreview
    override val recipients: Flow<IUser> = raw.recipients
        ?.map { User(it, kodein) }
        ?.asFlow() ?: throw NotPrivateChannelException()
}

class NotPrivateChannelException : IllegalArgumentException("Not a private channel")
class NotGuildChannelException : IllegalArgumentException("Not a guild channel")
package ru.tesserakt.diskordin.impl.core.entity

import arrow.core.Option
import arrow.core.toOption
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.GuildResponse
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.IChannel.Type.*
import ru.tesserakt.diskordin.impl.core.rest.service.GuildService
import ru.tesserakt.diskordin.util.Identified
import java.time.Duration

class Guild(raw: GuildResponse, override val kodein: Kodein) : IGuild {
    override val id: Snowflake = raw.id.asSnowflake()

    @FlowPreview
    override suspend fun findRole(id: Snowflake): Option<IRole> = roles
        .filter { it.id == id }
        .singleOrNull()
        .toOption()

    override val client: IDiscordClient by instance()
    override val iconHash: String? = raw.icon
    override val splashHash: String? = raw.splash

    @FlowPreview
    override val owner: Identified<IMember> = Identified(raw.owner_id.asSnowflake()) { id ->
        client.coroutineScope.async {
            members.filter { it.id == id }.single()
        }
    }

    @FlowPreview
    override val afkChannel: Identified<IVoiceChannel>? = raw.afk_channel_id?.asSnowflake()?.let {
        Identified(it) { id ->
            client.coroutineScope.async {
                channels.filter { channel -> channel.id == id }.single() as VoiceChannel
            }
        }
    }

    override val afkChannelTimeout: Duration = Duration.ofSeconds(raw.afk_timeout.toLong())

    override val verificationLevel = VerificationLevel.of(raw.verification_level)

    @FlowPreview
    override val roles: Flow<IRole> =
        raw.roles
            .map { Role(it, id, kodein) }
            .asFlow()

    @FlowPreview
    override val members: Flow<IMember> = flow {
        GuildService.Members
            .getMembers(id.asLong(), arrayOf("limit" to 1000L))
            .map { arr ->
                arr.map { Member(it, id, kodein) }
            }.fold(
                { throw it },
                { it.forEach { item -> emit(item) } }
            )
    }

    @FlowPreview
    override val channels: Flow<IGuildChannel> = flow {
        GuildService.Channels
            .getGuildChannels(id.asLong()).map { arr ->
                arr.map {
                    when (IChannel.Type.of(it.type)) {
                        GuildText -> TextChannel(it, kodein)
                        GuildVoice -> VoiceChannel(it, kodein)
                        GuildCategory, GuildNews, GuildStore -> TODO()
                        else -> throw IllegalArgumentException()
                    }
                }
            }.fold(
                { throw it },
                { it.forEach { item -> emit(item) } }
            )
    }

    override val name: String = raw.name

    override suspend fun delete(reason: String?) {
        GuildService.General.deleteGuild(id.asLong())
            .fold(
                { throw it },
                { null }
            )
    }
}
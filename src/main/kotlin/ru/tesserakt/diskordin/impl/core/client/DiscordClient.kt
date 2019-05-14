package ru.tesserakt.diskordin.impl.core.client

import arrow.core.orNull
import io.ktor.client.HttpClient
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import ru.tesserakt.diskordin.core.client.Diskordin
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.client.TokenType
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.UserResponse
import ru.tesserakt.diskordin.core.entity.IChannel
import ru.tesserakt.diskordin.core.entity.IChannel.Type
import ru.tesserakt.diskordin.core.entity.IGuild
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.core.rest.service.ChannelService
import ru.tesserakt.diskordin.core.rest.service.GuildService
import ru.tesserakt.diskordin.core.rest.service.UserService
import ru.tesserakt.diskordin.impl.core.entity.*
import ru.tesserakt.diskordin.util.Identified

data class DiscordClient(
    override val token: String,
    override val tokenType: TokenType,
    private val httpClient: HttpClient
) : IDiscordClient {

    override lateinit var self: Identified<IUser>

    override var isConnected: Boolean = false
        private set

    @FlowPreview
    override val users: Flow<IUser> = TODO()
    @FlowPreview
    override val guilds: Flow<IGuild> = TODO()

    override suspend fun login() {
        val rawSelf = UserService.General.getCurrentUser().fold(
            { (throw it) as UserResponse },
            { return@fold it }
        )
        self = Identified(rawSelf.id.asSnowflake()) {
            User(rawSelf, Diskordin.kodein)
        }
        isConnected = true
    }

    override fun logout() {
        httpClient.close()
    }

    override suspend fun findUser(id: Snowflake) =
        UserService.General
            .getUser(id.asLong())
            .map {
                User(it, Diskordin.kodein)

            }
            .orNull()

    override suspend fun findGuild(id: Snowflake): IGuild? =
        GuildService.General.getGuild(id.asLong())
            .map { Guild(it, Diskordin.kodein) }
            .orNull()

    override suspend fun findChannel(id: Snowflake): IChannel? = ChannelService.General.getChannel(id.asLong())
        .map {
            when (Type.of(it.type)) {
                Type.GuildText -> TextChannel(it, Diskordin.kodein)
                Type.Private -> PrivateChannel(it, Diskordin.kodein)
                Type.GuildVoice -> VoiceChannel(it, Diskordin.kodein)
                Type.PrivateGroup -> TODO()
                Type.GuildCategory -> TODO()
                Type.GuildNews -> TODO()
                Type.GuildStore -> TODO()
            }
        }.orNull()

    override suspend fun createGuild(): IGuild = TODO()
}
package ru.tesserakt.diskordin.impl.core.client

import arrow.core.Option
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import ru.tesserakt.diskordin.Diskordin
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.client.TokenType
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.request.GuildCreateRequest
import ru.tesserakt.diskordin.core.data.json.response.UserResponse
import ru.tesserakt.diskordin.core.entity.IChannel
import ru.tesserakt.diskordin.core.entity.IChannel.Type
import ru.tesserakt.diskordin.core.entity.IGuild
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.impl.core.entity.*
import ru.tesserakt.diskordin.impl.core.rest.service.ChannelService
import ru.tesserakt.diskordin.impl.core.rest.service.GuildService
import ru.tesserakt.diskordin.impl.core.rest.service.UserService
import ru.tesserakt.diskordin.util.Identified

data class DiscordClient(
    override val token: String,
    override val tokenType: TokenType,
    private val httpClient: HttpClient
) : IDiscordClient {
    override val coroutineScope = CoroutineScope(Dispatchers.IO)

    override lateinit var self: Identified<IUser>

    override var isConnected: Boolean = false
        private set

    @FlowPreview
    override val users: Flow<IUser> = emptyArray<User>().asFlow()
    @FlowPreview
    override val guilds: Flow<IGuild> = emptyArray<Guild>().asFlow()

    override suspend fun login() {
        val rawSelf = UserService.General.getCurrentUser().fold<UserResponse>(
            { (throw it) },
            { return@fold it }
        )
        self = Identified(rawSelf.id.asSnowflake()) {
            coroutineScope.async {
                User(rawSelf, Diskordin.kodein)
            }
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
            }.toOption()

    override suspend fun findGuild(id: Snowflake): Option<IGuild> =
        GuildService.General.getGuild(id.asLong())
            .map { Guild(it, Diskordin.kodein) }
            .toOption()

    override suspend fun findChannel(id: Snowflake): Option<IChannel> = ChannelService.General.getChannel(id.asLong())
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
        }.toOption()

    override suspend fun createGuild(request: GuildCreateRequest): IGuild = TODO()
}
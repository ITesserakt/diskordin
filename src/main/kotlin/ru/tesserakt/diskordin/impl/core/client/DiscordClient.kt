package ru.tesserakt.diskordin.impl.core.client

import arrow.core.Option
import arrow.core.getOrElse
import arrow.data.handleLeftWith
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
import ru.tesserakt.diskordin.core.data.json.request.GuildCreateRequest
import ru.tesserakt.diskordin.core.entity.IChannel
import ru.tesserakt.diskordin.core.entity.IChannel.Type
import ru.tesserakt.diskordin.core.entity.IGuild
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.impl.core.client.TokenVerification.VerificationError.*
import ru.tesserakt.diskordin.impl.core.entity.*
import ru.tesserakt.diskordin.impl.core.rest.service.ChannelService
import ru.tesserakt.diskordin.impl.core.rest.service.GuildService
import ru.tesserakt.diskordin.impl.core.rest.service.UserService
import ru.tesserakt.diskordin.util.Identified
import ru.tesserakt.diskordin.util.Loggers

data class DiscordClient(
    override val token: String,
    override val tokenType: TokenType,
    private val httpClient: HttpClient
) : IDiscordClient {
    private val logger by Loggers

    init {
        TokenVerification(token, tokenType).verify().map {
            self = Identified(it) {
                coroutineScope.async {
                    findUser(it).getOrElse { throw IllegalArgumentException("Illegal token!") }
                }
            }
        }.handleLeftWith {
            when (it) {
                BlankString -> logger.error("Token cannot be blank")
                CorruptedId -> logger.error("Token is corrupted!")
                InvalidCharacters -> logger.error("Token contains invalid characters!")
                InvalidConstruction -> logger.error("Token does not fit into right form!")
            }
            throw IllegalStateException()
        }
    }

    override val coroutineScope = CoroutineScope(Dispatchers.IO)

    override lateinit var self: Identified<IUser>

    override var isConnected: Boolean = false
        private set

    @FlowPreview
    override val users: Flow<IUser> = emptyArray<User>().asFlow()
    @FlowPreview
    override val guilds: Flow<IGuild> = emptyArray<Guild>().asFlow()

    override suspend fun login() {
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
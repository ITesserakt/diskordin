package ru.tesserakt.diskordin.impl.core.client

import arrow.data.handleLeftWith
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.client.TokenType
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.IChannel
import ru.tesserakt.diskordin.core.entity.IGuild
import ru.tesserakt.diskordin.core.entity.ISelf
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.core.entity.`object`.IInvite
import ru.tesserakt.diskordin.core.entity.`object`.IRegion
import ru.tesserakt.diskordin.core.entity.builder.GuildCreateBuilder
import ru.tesserakt.diskordin.impl.core.client.TokenVerification.VerificationError.*
import ru.tesserakt.diskordin.impl.core.entity.Guild
import ru.tesserakt.diskordin.impl.core.entity.Self
import ru.tesserakt.diskordin.impl.core.entity.User
import ru.tesserakt.diskordin.impl.core.service.*
import ru.tesserakt.diskordin.util.Identified
import ru.tesserakt.diskordin.util.Loggers

data class DiscordClient(
    override val token: String,
    override val tokenType: TokenType,
    private val httpClient: HttpClient
) : IDiscordClient {
    private val logger by Loggers

    init {
        TokenVerification(token, tokenType).verify().map { id ->
            self = Identified(id) {
                findUser(it)!! as Self
            }
        }.handleLeftWith {
            val message = when (it) {
                BlankString -> "Token cannot be blank"
                CorruptedId -> "Token is corrupted!"
                InvalidCharacters -> "Token contains invalid characters!"
                InvalidConstruction -> "Token does not fit into right form!"
            }
            logger.error(message)
            throw IllegalStateException(message)
        }
    }

    override val coroutineScope = CoroutineScope(Dispatchers.IO)

    override lateinit var self: Identified<ISelf>

    override var isConnected: Boolean = false
        private set

    @ExperimentalCoroutinesApi
    override val users: Flow<IUser> = emptyArray<User>().asFlow()
    @ExperimentalCoroutinesApi
    override val guilds: Flow<IGuild> = emptyArray<Guild>().asFlow()

    override suspend fun login() {
        isConnected = true
    }

    override fun logout() {

        httpClient.close()
    }

    override suspend fun findUser(id: Snowflake) =
        UserService.getUser(id)

    override suspend fun findGuild(id: Snowflake) =
        GuildService.getGuild(id)

    override suspend fun findChannel(id: Snowflake): IChannel? =
        ChannelService.getChannel(id)

    override suspend fun createGuild(request: GuildCreateBuilder.() -> Unit): IGuild = GuildService.createGuild(request)

    override suspend fun getInvite(code: String): IInvite? = kotlin.runCatching {
        InviteService.getInvite(code)
    }.getOrNull()

    override suspend fun deleteInvite(code: String, reason: String?) =
        InviteService.deleteInvite(code, reason)

    override suspend fun getRegions(): List<IRegion> = VoiceService.getVoiceRegions()
}
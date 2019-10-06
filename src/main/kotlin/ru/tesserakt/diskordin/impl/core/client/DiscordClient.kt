package ru.tesserakt.diskordin.impl.core.client

import arrow.data.handleLeftWith
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
import ru.tesserakt.diskordin.gateway.Gateway
import ru.tesserakt.diskordin.impl.core.client.TokenVerification.VerificationError.*
import ru.tesserakt.diskordin.impl.core.entity.Guild
import ru.tesserakt.diskordin.impl.core.entity.User
import ru.tesserakt.diskordin.impl.core.service.*
import ru.tesserakt.diskordin.rest.resource.GatewayResource
import ru.tesserakt.diskordin.util.Identified
import ru.tesserakt.diskordin.util.Loggers
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

data class DiscordClient(
    override val tokenType: TokenType
) : IDiscordClient {
    private val logger by Loggers
    override val token: String = getKoin().getProperty("token")!!

    init {
        TokenVerification(token, tokenType).verify().map { id ->
            self = Identified(id) {
                UserService.getCurrentUser()
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

    override lateinit var self: Identified<ISelf>
        private set

    override var isConnected: Boolean = false
        private set

    override lateinit var gateway: Gateway
        private set

    override val users: Flow<IUser> = emptyArray<User>().asFlow()
    override val guilds: Flow<IGuild> = emptyArray<Guild>().asFlow()

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    override suspend fun login() {
        val gatewayURL = GatewayResource.General.getGatewayURL().url
        val metadata = GatewayResource.General.getGatewayBot().sessionMeta
        this.gateway = Gateway(gatewayURL, metadata.total, metadata.remaining, metadata.resetAfter.milliseconds)
        isConnected = true
        this.gateway.run()
    }

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    override suspend fun use(block: suspend IDiscordClient.() -> Unit) {
        login()
        block()
        logout()
    }

    override fun logout() {
        logger.info("Shutting down gateway")
        gateway.stop()
    }

    override suspend fun findUser(id: Snowflake) =
        UserService.getUser(id)

    override suspend fun findGuild(id: Snowflake) =
        GuildService.getGuild(id)

    override suspend fun findChannel(id: Snowflake): IChannel? =
        ChannelService.getChannel(id)

    override suspend fun createGuild(request: GuildCreateBuilder.() -> Unit): IGuild = GuildService.createGuild(request)

    override suspend fun getInvite(code: String): IInvite? = runCatching {
        InviteService.getInvite(code)
    }.getOrNull()

    override suspend fun deleteInvite(code: String, reason: String?) =
        InviteService.deleteInvite(code, reason)

    override suspend fun getRegions(): List<IRegion> = VoiceService.getVoiceRegions()
}
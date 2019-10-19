package ru.tesserakt.diskordin.impl.core.client

import arrow.core.handleLeftWith
import arrow.fx.IO
import arrow.fx.extensions.io.async.async
import arrow.fx.fix
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.tesserakt.diskordin.core.client.EventDispatcher
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.client.TokenType
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.`object`.IInvite
import ru.tesserakt.diskordin.core.entity.`object`.IRegion
import ru.tesserakt.diskordin.core.entity.builder.GuildCreateBuilder
import ru.tesserakt.diskordin.core.entity.builder.build
import ru.tesserakt.diskordin.gateway.Gateway
import ru.tesserakt.diskordin.impl.core.client.TokenVerification.VerificationError.*
import ru.tesserakt.diskordin.util.Identified
import ru.tesserakt.diskordin.util.Loggers
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime

data class DiscordClient(
    override val tokenType: TokenType
) : IDiscordClient {
    @ExperimentalCoroutinesApi
    override lateinit var eventDispatcher: EventDispatcher
    private val logger by Loggers
    override val token: String = getKoin().getProperty("token")!!

    init {
        TokenVerification(token, tokenType).verify().map { id ->
            self = Identified(id) {
                userService.getCurrentUser().unwrap()
            }
            logger.info("Token verified")
        }.handleLeftWith {
            val message = when (it) {
                BlankString -> "Token cannot be blank"
                CorruptedId -> "Token is corrupted!"
                InvalidCharacters -> "Token contains invalid characters!"
                InvalidConstruction -> "Token does not fit into right form!"
            }
            throw IllegalStateException(message)
        }
    }

    override lateinit var self: Identified<ISelf>
        private set

    override var isConnected: Boolean = false
        private set

    @ExperimentalCoroutinesApi
    override lateinit var gateway: Gateway
        private set

    override val users: Flow<IUser> = flowOf()
    override val guilds: Flow<IGuild> = flowOf()

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    override suspend fun login() {
        val gatewayStats = gatewayService.getGatewayBot().unwrap()
        val gatewayURL = gatewayStats.url
        val metadata = gatewayStats.session
        this.gateway = Gateway(gatewayURL, metadata.total, metadata.remaining, metadata.resetAfter)
        eventDispatcher = gateway.eventDispatcher
        isConnected = true
        this.gateway.run()
    }

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    override suspend fun use(block: suspend IDiscordClient.() -> Unit) {
        isConnected = true
        block()
        logout()
    }

    @ExperimentalCoroutinesApi
    override fun logout() {
        logger.info("Shutting down gateway")
        if (this::gateway.isInitialized)
            gateway.stop()
        exitProcess(0)
    }

    override suspend fun findUser(id: Snowflake) =
        userService.getUser(id).unwrap("User")

    override suspend fun findGuild(id: Snowflake) =
        guildService.getGuild(id).unwrap()

    override suspend fun findChannel(id: Snowflake): IChannel =
        channelService.getChannel<IChannel>(id).unwrap()

    override suspend fun createGuild(request: GuildCreateBuilder.() -> Unit): IGuild =
        guildService.createGuild(request.build()).unwrap()

    override suspend fun getInvite(code: String): IInvite =
        inviteService.getInvite(code).unwrap()

    override suspend fun deleteInvite(code: String, reason: String?) =
        inviteService.deleteInvite(code)

    override suspend fun getRegions(): List<IRegion> = voiceService.getVoiceRegions().async(IO.async())
        .fix().suspended()
        .body()!!.map { it.unwrap() }
}
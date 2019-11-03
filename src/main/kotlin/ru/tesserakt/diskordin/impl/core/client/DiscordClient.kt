package ru.tesserakt.diskordin.impl.core.client

import arrow.core.Id
import arrow.core.ListK
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.functor.functor
import arrow.core.extensions.listk.functor.functor
import arrow.core.fix
import arrow.core.handleLeftWith
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.monad.map
import arrow.fx.fix
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.core.inject
import ru.tesserakt.diskordin.core.client.EventDispatcher
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.client.TokenType
import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.identify
import ru.tesserakt.diskordin.core.entity.IGuild
import ru.tesserakt.diskordin.core.entity.ISelf
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.core.entity.`object`.IInvite
import ru.tesserakt.diskordin.core.entity.`object`.IRegion
import ru.tesserakt.diskordin.core.entity.builder.GuildCreateBuilder
import ru.tesserakt.diskordin.core.entity.builder.build
import ru.tesserakt.diskordin.gateway.Gateway
import ru.tesserakt.diskordin.impl.core.client.TokenVerification.VerificationError.*
import ru.tesserakt.diskordin.rest.RestClient
import ru.tesserakt.diskordin.rest.call
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
    override val rest: RestClient<ForIO> by inject()

    init {
        TokenVerification(token, tokenType).verify().map { id ->
            self = id identify {
                rest.call(Id.functor()) {
                    userService.getCurrentUser()
                }.bind().extract()
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

    override lateinit var users: IO<ListK<IUser>>
        private set
    override lateinit var guilds: IO<ListK<IGuild>>
        private set

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    override fun login() = IO.fx {
        val gatewayStats = rest.call(Id.functor()) { gatewayService.getGatewayBot() }.bind().extract()
        val gatewayURL = gatewayStats.url
        val metadata = gatewayStats.session
        this@DiscordClient.gateway = Gateway(gatewayURL, metadata.total, metadata.remaining, metadata.resetAfter)
        eventDispatcher = gateway.eventDispatcher
        isConnected = true

        !effect { this@DiscordClient.gateway.run().join() }
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
        if (this::gateway.isInitialized) {
            logger.info("Shutting down gateway")
            gateway.stop()
        }
        exitProcess(0)
    }

    override fun getUser(id: Snowflake) = rest.call(Id.functor()) {
        userService.getUser(id)
    }.map { it.extract() }

    override fun getGuild(id: Snowflake) = rest.call(Id.functor()) {
        guildService.getGuild(id)
    }.map { it.extract() }

    override fun getChannel(id: Snowflake) = rest.call(Id.functor()) {
        channelService.getChannel(id)
    }.map { it.extract() }

    override fun createGuild(request: GuildCreateBuilder.() -> Unit) = rest.call(Id.functor()) {
        guildService.createGuild(request.build())
    }.map { it.extract() }

    override fun getInvite(code: String): IO<IInvite> = rest.call(Id.functor()) {
        inviteService.getInvite(code)
    }.map { it.extract() }

    override fun deleteInvite(code: String, reason: String?) = rest.effect {
        inviteService.deleteInvite(code)
    }.fix()

    override fun getRegions(): IO<ListK<IRegion>> = rest.call(ListK.functor()) {
        voiceService.getVoiceRegions()
    }.map { it.fix() }
}
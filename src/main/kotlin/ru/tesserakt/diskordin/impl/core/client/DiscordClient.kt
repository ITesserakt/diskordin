package ru.tesserakt.diskordin.impl.core.client

import arrow.core.*
import arrow.core.extensions.either.monad.flatTap
import arrow.core.extensions.either.monadError.monadError
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.functor.functor
import arrow.core.extensions.listk.functor.functor
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.applicative.just
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
    override val self: Identified<ISelf>
    override val rest: RestClient<ForIO> by inject()

    init {
        self = TokenVerification(token, tokenType, Either.monadError())
            .verify()
            .flatTap { logger.info("Token verified").right() }
            .getOrHandle {
                throw error(it.message)
            } identify {
            rest.call(Id.functor()) { userService.getCurrentUser() }.bind().extract()
        }
    }

    override var isConnected: Boolean = false
        private set

    @ExperimentalCoroutinesApi
    override lateinit var gateway: Gateway
        private set

    override val users = mutableListOf<IUser>().just()
    override val guilds = mutableListOf<IGuild>().just()

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    override fun login(): IO<Unit> = IO.fx {
        val gatewayStats = rest.call(Id.functor()) {
            gatewayService.getGatewayBot()
        }.bind().extract()
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

    override fun createGuild(request: GuildCreateBuilder.() -> Unit): IO<IGuild> = rest.call(Id.functor()) {
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

    override fun getChannel(id: Snowflake) = rest.call(Id.functor()) {
        channelService.getChannel(id)
    }.map { it.extract() }

    override fun getGuild(id: Snowflake) = rest.call(Id.functor()) {
        guildService.getGuild(id)
    }.map { it.extract() }

    override fun getUser(id: Snowflake) = rest.call(Id.functor()) {
        userService.getUser(id)
    }.map { it.extract() }
}
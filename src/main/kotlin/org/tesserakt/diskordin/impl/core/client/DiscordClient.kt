package org.tesserakt.diskordin.impl.core.client

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
import arrow.fx.extensions.io.functor.map
import arrow.fx.fix
import arrow.fx.typeclasses.ConcurrentSyntax
import kotlinx.coroutines.ExperimentalCoroutinesApi
import mu.KLogging
import org.koin.core.inject
import org.tesserakt.diskordin.core.client.EventDispatcher
import org.tesserakt.diskordin.core.client.IDiscordClient
import org.tesserakt.diskordin.core.data.Identified
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.core.entity.ISelf
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.`object`.IInvite
import org.tesserakt.diskordin.core.entity.`object`.IRegion
import org.tesserakt.diskordin.core.entity.builder.GuildCreateBuilder
import org.tesserakt.diskordin.core.entity.builder.build
import org.tesserakt.diskordin.gateway.Gateway
import org.tesserakt.diskordin.rest.RestClient
import org.tesserakt.diskordin.rest.call
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime

class DiscordClient : IDiscordClient {
    @ExperimentalCoroutinesApi
    override lateinit var eventDispatcher: EventDispatcher
    override val token: String = getKoin().getProperty("token")!!
    override lateinit var self: Identified<ISelf>
        private set
    override val rest: RestClient<ForIO> by inject()

    private companion object : KLogging()

    init {
        self = TokenVerification(token, Either.monadError())
            .verify()
            .flatTap { logger.info("Token verified").right() }
            .getOrHandle {
                error(it.message)
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
    override fun login() = IO.fx {
        val gatewayStats = rest.call(Id.functor()) {
            gatewayService.getGatewayBot()
        }.bind().extract()
        val gatewayURL = gatewayStats.url
        val metadata = gatewayStats.session
        this@DiscordClient.gateway = Gateway(gatewayURL, metadata.total, metadata.remaining, metadata.resetAfter)
        eventDispatcher = gateway.eventDispatcher
        isConnected = true

        this@DiscordClient.gateway.run()
        Unit
    }

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    override fun use(block: ConcurrentSyntax<ForIO>.(IDiscordClient) -> Unit) = IO.fx {
        isConnected = true
        this.block(this@DiscordClient)
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

    override fun getUser(id: Snowflake) = rest.call {
        userService.getUser(id)
    }.fix()
}
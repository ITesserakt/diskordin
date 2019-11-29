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
import arrow.fx.rx2.FlowableK
import arrow.fx.rx2.ForFlowableK
import arrow.fx.rx2.extensions.flowablek.applicative.applicative
import arrow.fx.rx2.extensions.flowablek.applicative.map
import arrow.fx.rx2.extensions.flowablek.async.async
import mu.KLogging
import org.koin.core.inject
import org.tesserakt.diskordin.core.client.EventDispatcher
import org.tesserakt.diskordin.core.client.IDiscordClient
import org.tesserakt.diskordin.core.client.TokenType
import org.tesserakt.diskordin.core.client.WebSocketStateHolder
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
import org.tesserakt.diskordin.gateway.json.*
import org.tesserakt.diskordin.impl.gateway.interpreter.flowableInterpreter
import org.tesserakt.diskordin.rest.RestClient
import org.tesserakt.diskordin.rest.call
import kotlin.system.exitProcess

data class DiscordClient(
    override val tokenType: TokenType
) : IDiscordClient {
    override val eventDispatcher: EventDispatcher<ForFlowableK> = EventDispatcherImpl(FlowableK.async())
    override val webSocketStateHolder: WebSocketStateHolder = WebSocketStateHolderImpl()
    override val token: String = getKoin().getProperty("token")!!
    override val self: Identified<ISelf>
    override val rest: RestClient<ForIO> by inject()

    private companion object : KLogging()

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

    override lateinit var gateway: Gateway
        private set

    override val users = mutableListOf<IUser>().just()
    override val guilds = mutableListOf<IGuild>().just()

    @Suppress("UNCHECKED_CAST")
    @ExperimentalStdlibApi
    override fun login() = IO.fx {
        val gatewayUrl = !rest.call { gatewayService.getGatewayBot() }
        val (gateway, impl) = Gateway.create(
            gatewayUrl.url,
            "zlib-stream"
        )
        this@DiscordClient.gateway = gateway
        isConnected = true

        gateway.run(impl.flowableInterpreter).fold(FlowableK.applicative())
            .map { payload: Payload<out IPayload> ->
                if (payload.opcode() == Opcode.UNDERLYING) {
                    webSocketStateHolder.update(payload as Payload<IToken>)
                } else {
                    eventDispatcher.publish(payload as Payload<IRawEvent>)
                }
            }.flowable.subscribe()

        Unit
    }.unsafeRunSync()

    override suspend fun use(block: suspend IDiscordClient.() -> Unit) {
        isConnected = true
        this.block()
        logout()
    }

    override fun logout() {
        if (this::gateway.isInitialized) {
            logger.info("Shutting down gateway")
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
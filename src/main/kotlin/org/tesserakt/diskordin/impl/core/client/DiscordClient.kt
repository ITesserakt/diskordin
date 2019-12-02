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
import arrow.fx.rx2.extensions.flowablek.dispatchers.dispatchers
import arrow.fx.rx2.fix
import arrow.fx.typeclasses.ConcurrentSyntax
import mu.KLogging
import org.koin.core.inject
import org.tesserakt.diskordin.core.client.EventDispatcher
import org.tesserakt.diskordin.core.client.IDiscordClient
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
import org.tesserakt.diskordin.gateway.Gateway
import org.tesserakt.diskordin.gateway.Implementation
import org.tesserakt.diskordin.gateway.json.*
import org.tesserakt.diskordin.gateway.json.token.ConnectionClosed
import org.tesserakt.diskordin.gateway.json.token.ConnectionFailed
import org.tesserakt.diskordin.gateway.json.token.ConnectionOpened
import org.tesserakt.diskordin.gateway.json.token.NoConnection
import org.tesserakt.diskordin.impl.gateway.handler.handleHello
import org.tesserakt.diskordin.impl.gateway.handler.heartbeatACKHandler
import org.tesserakt.diskordin.impl.gateway.handler.heartbeatHandler
import org.tesserakt.diskordin.impl.gateway.handler.restartHandler
import org.tesserakt.diskordin.impl.gateway.interpreter.flowableInterpreter
import org.tesserakt.diskordin.impl.util.typeclass.flowablek.generative.generative
import org.tesserakt.diskordin.rest.RestClient
import org.tesserakt.diskordin.rest.call
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime

class DiscordClient : IDiscordClient {
    override val eventDispatcher: EventDispatcher<ForFlowableK> = EventDispatcherImpl(FlowableK.generative())
    override val webSocketStateHolder: WebSocketStateHolder = WebSocketStateHolderImpl()
    override val token: String = getKoin().getProperty("token")!!
    override val self: Identified<ISelf>
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

        webSocketStateHolder.observe { _, new ->
            when (new) {
                is ConnectionOpened -> logger.info("Gateway reached")
                is ConnectionClosed -> logger.warn("Gateway closed: ${new.reason}")
                is ConnectionFailed -> logger.error("Gateway met with error", new.error)
            }
        }
    }

    override var isConnected: Boolean = false
        private set

    override val gateway: Gateway = Gateway()

    override val users = mutableListOf<IUser>().just()
    override val guilds = mutableListOf<IGuild>().just()

    @ExperimentalTime
    @Suppress("UNCHECKED_CAST")
    @ExperimentalStdlibApi
    override fun login() = IO.fx {
        val gatewayUrl = !rest.call { gatewayService.getGatewayBot() }
        val (_, impl) = Gateway.create(
            gatewayUrl.url,
            "zlib-stream"
        )
        isConnected = true

        GlobalGatewayLifecycle.start()
        gateway.run(impl.flowableInterpreter).fold(FlowableK.applicative())
            .map { payload: Payload<out IPayload> ->
                if (payload.opcode() == Opcode.UNDERLYING) {
                    webSocketStateHolder.update(payload as Payload<IToken>)
                } else {
                    eventDispatcher.publish(payload as Payload<IRawEvent>).mapLeft { IllegalStateException(it.message) }
                }
            }.flatMap { either ->
                either.fold(
                    { FlowableK.raiseError<Any>(it) },
                    { FlowableK.just(it) }
                )
            }.flowable.subscribe()

        launchDefaultEventHandlers(impl)

        Unit
    }


    @ExperimentalTime
    private fun launchDefaultEventHandlers(impl: Implementation) {
        eventDispatcher.handleHello(
            token,
            gateway::sequenceId,
            impl.flowableInterpreter,
            FlowableK.async(),
            FlowableK.dispatchers()
        ).fix().flowable.subscribe()

        eventDispatcher.heartbeatHandler(gateway::sequenceId, impl.flowableInterpreter, FlowableK.async()).fix()
            .flowable.subscribe()

        eventDispatcher.heartbeatACKHandler(webSocketStateHolder, FlowableK.async()).fix().flowable.subscribe()

        eventDispatcher.restartHandler(
            token,
            gateway::sequenceId,
            webSocketStateHolder,
            impl.flowableInterpreter,
            FlowableK.async()
        ).fix().flowable.subscribe()
    }

    override fun use(block: suspend ConcurrentSyntax<ForIO>.(IDiscordClient) -> Unit) = IO.fx {
        isConnected = true
        this.block(this@DiscordClient)
        logout()
    }

    override fun logout() {
        if (webSocketStateHolder.getState() != NoConnection) {
            logger.info("Shutting down gateway")
            GlobalGatewayLifecycle.stop()
        }
        exitProcess(0)
    }

    override fun createGuild(
        name: String,
        region: IRegion,
        icon: String,
        verificationLevel: IGuild.VerificationLevel,
        defaultMessageNotificationLevel: IGuild.DefaultMessageNotificationLevel,
        explicitContentFilter: IGuild.ExplicitContentFilter,
        builder: GuildCreateBuilder.() -> Unit
    ): IO<IGuild> = rest.call {
        val inst = GuildCreateBuilder(
            name,
            region.name,
            icon,
            verificationLevel,
            defaultMessageNotificationLevel,
            explicitContentFilter
        ).apply(builder)
        guildService.createGuild(inst.create())
    }.fix()

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
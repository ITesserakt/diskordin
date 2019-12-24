package org.tesserakt.diskordin.impl.core.client

import arrow.core.*
import arrow.core.extensions.either.monad.flatTap
import arrow.core.extensions.either.monadError.monadError
import arrow.core.extensions.listk.functor.functor
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.Ref
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.applicative.just
import arrow.fx.extensions.io.functor.map
import arrow.fx.extensions.io.monad.flatTap
import arrow.fx.extensions.io.monadDefer.monadDefer
import arrow.fx.fix
import arrow.fx.rx2.FlowableK
import arrow.fx.rx2.ForFlowableK
import arrow.fx.rx2.extensions.concurrent
import arrow.fx.rx2.extensions.flowablek.async.async
import arrow.fx.rx2.extensions.flowablek.async.continueOn
import arrow.fx.rx2.fix
import arrow.fx.typeclasses.ConcurrentSyntax
import mu.KotlinLogging
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.core.client.EventDispatcher
import org.tesserakt.diskordin.core.client.IDiscordClient
import org.tesserakt.diskordin.core.client.WebSocketStateHolder
import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.`object`.IInvite
import org.tesserakt.diskordin.core.entity.`object`.IRegion
import org.tesserakt.diskordin.core.entity.builder.GuildCreateBuilder
import org.tesserakt.diskordin.gateway.Gateway
import org.tesserakt.diskordin.gateway.GatewayCompiler
import org.tesserakt.diskordin.gateway.json.*
import org.tesserakt.diskordin.gateway.json.token.ConnectionClosed
import org.tesserakt.diskordin.gateway.json.token.ConnectionFailed
import org.tesserakt.diskordin.gateway.json.token.ConnectionOpened
import org.tesserakt.diskordin.gateway.json.token.NoConnection
import org.tesserakt.diskordin.impl.gateway.handler.handleHello
import org.tesserakt.diskordin.impl.gateway.handler.heartbeatACKHandler
import org.tesserakt.diskordin.impl.gateway.handler.heartbeatHandler
import org.tesserakt.diskordin.impl.gateway.interpreter.flowableInterpreter
import org.tesserakt.diskordin.impl.util.typeclass.flowablek.generative.generative
import org.tesserakt.diskordin.rest.RestClient
import org.tesserakt.diskordin.rest.call
import org.tesserakt.diskordin.rest.storage.GlobalEntityCache
import org.tesserakt.diskordin.rest.storage.GlobalInviteCache
import org.tesserakt.diskordin.rest.storage.GlobalMemberCache
import org.tesserakt.diskordin.util.toJsonTree
import kotlin.time.ExperimentalTime

internal class DiscordClient private constructor(
    private val context: BootstrapContext<ForIO>
) : IDiscordClient {
    companion object {
        internal val client = Ref.unsafe<ForIO, DiscordClient?>(null, IO.monadDefer())

        operator fun invoke(context: BootstrapContext<ForIO>) = client.updateAndGet {
            if (it != null) throw IllegalStateException("Discord client already created")
            DiscordClient(context)
        }.map { it!! }
    }

    override val eventDispatcher: EventDispatcher<ForFlowableK> = EventDispatcherImpl(FlowableK.generative())
    override val webSocketStateHolder: WebSocketStateHolder = WebSocketStateHolderImpl()
    override val token: String = context.token
    override val rest: RestClient<ForIO> = context.restClient
    private val gatewayImplementation = Gateway.create(context.gatewayContext)

    private val gateway = gatewayImplementation.a
    private val impl = gatewayImplementation.b
    private val logger = KotlinLogging.logger("[Discord client]")

    override val self: IdentifiedF<ForIO, ISelf> = token.verify(Either.monadError())
        .flatTap { logger.info("Token verified").right() }
        .getOrHandle {
            error(it.message)
        } identify {
        rest.call { userService.getCurrentUser() }
    }

    init {
        webSocketStateHolder.observe { _, new ->
            when (new) {
                is ConnectionOpened -> logger.info("Gateway reached")
                is ConnectionClosed -> logger.warn("Gateway closed: ${new.reason}")
                is ConnectionFailed -> logger.error("Gateway met with error", new.error)
            }
        }
    }

    override val users get() = (GlobalEntityCache + GlobalMemberCache).values.filterIsInstance<IUser>()
    override val guilds get() = GlobalEntityCache.values.filterIsInstance<IGuild>()

    @ExperimentalTime
    @Suppress("UNCHECKED_CAST")
    @ExperimentalStdlibApi
    override fun login() = IO.fx io@{
        gateway.run(impl.flowableInterpreter, FlowableK.async()).fix().map { payload ->
            if (payload.opcode() == Opcode.UNDERLYING)
                webSocketStateHolder.update(payload as Payload<in IToken>)
            else
                eventDispatcher.publish(payload as Payload<IRawEvent>)
        }.flowable.doOnError {
            webSocketStateHolder.update(
                Payload(
                    Opcode.UNDERLYING.asInt(),
                    null,
                    "CONNECTION_FAILED",
                    ConnectionFailed(it).toJsonTree()
                )
            )
        }.subscribe()

        launchDefaultEventHandlers(impl.flowableInterpreter)
        Unit
    }

    @ExperimentalTime
    private fun launchDefaultEventHandlers(compiler: GatewayCompiler<ForFlowableK>) = with(eventDispatcher) {
        handleHello(
            token,
            context.gatewayContext.connectionContext.compression.isNotEmpty(),
            webSocketStateHolder,
            gateway::sequenceId,
            compiler,
            FlowableK.concurrent()
        ).continueOn(context.gatewayContext.scheduler).flowable.subscribe()
        heartbeatHandler(gateway::sequenceId, compiler, FlowableK.async())
            .continueOn(context.gatewayContext.scheduler).flowable.subscribe()
        heartbeatACKHandler(webSocketStateHolder, FlowableK.async())
            .continueOn(context.gatewayContext.scheduler).flowable.subscribe()
    }

    override fun use(block: suspend ConcurrentSyntax<ForIO>.(IDiscordClient) -> Unit) = IO.fx {
        this.block(this@DiscordClient)
        logout()
    }

    override fun logout() {
        if (webSocketStateHolder.getState() != NoConnection) {
            logger.info("Shutting down gateway")
            GlobalGatewayLifecycle.stop()
        }
        DiscordClient.client.set(null).fix().unsafeRunSync()
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

    override fun getInvite(code: String): IO<IInvite> = GlobalInviteCache[code]?.just()
        ?: rest.call { inviteService.getInvite(code) }.flatTap { GlobalInviteCache[code] = it; just() }

    override fun deleteInvite(code: String, reason: String?) =
        rest.effect { inviteService.deleteInvite(code) }.flatTap { GlobalInviteCache -= code; just() }

    override fun getRegions(): IO<ListK<IRegion>> = rest.call(ListK.functor()) {
        voiceService.getVoiceRegions()
    }.map { it.fix() }

    override fun getChannel(id: Snowflake) = (GlobalEntityCache[id] as IChannel?)?.just()
        ?: rest.call { channelService.getChannel(id) }.flatTap { GlobalEntityCache[id] = it; just() }

    override fun getGuild(id: Snowflake) = guilds.find { it.id == id }?.just()
        ?: rest.call { guildService.getGuild(id) }.flatTap { GlobalEntityCache[id] = it; just() }

    override fun getUser(id: Snowflake) =
        users.find { it.id == id }?.just() ?: rest.call { userService.getUser(id) }.flatTap {
            GlobalEntityCache[id] = it; just()
        }

    override fun getMember(userId: Snowflake, guildId: Snowflake) =
        GlobalMemberCache[guildId to userId]?.just() ?: getUser(userId).flatMap { it.asMember(guildId) }.flatTap {
            GlobalMemberCache[guildId to userId] = it; just()
        }

    override fun getMessage(channelId: Snowflake, messageId: Snowflake): IO<IMessage> =
        GlobalEntityCache.values.filterIsInstance<IMessage>()
            .filter { it.channel.id == channelId }
            .find { it.id == messageId }?.just()
            ?: rest.call {
                channelService.getMessage(channelId, messageId)
            }.flatTap { GlobalEntityCache += it.id to it; just() }
}
package org.tesserakt.diskordin.impl.core.client

import arrow.core.Either
import arrow.core.ListK
import arrow.core.extensions.either.monadError.monadError
import arrow.core.extensions.list.foldable.find
import arrow.core.extensions.listk.functor.functor
import arrow.core.fix
import arrow.core.getOrHandle
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.Ref
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.applicative.just
import arrow.fx.extensions.io.functor.map
import arrow.fx.extensions.io.monad.flatTap
import arrow.fx.extensions.io.monadDefer.monadDefer
import arrow.fx.fix
import arrow.fx.typeclasses.ConcurrentSyntax
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.tesserakt.diskordin.core.client.BootstrapContext
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
import org.tesserakt.diskordin.rest.RestClient
import org.tesserakt.diskordin.rest.call

internal class DiscordClient<F> private constructor(
    internal val context: BootstrapContext<ForIO, F>
) : IDiscordClient {
    companion object {
        internal val client = Ref.unsafe<ForIO, DiscordClient<*>?>(null, IO.monadDefer())

        operator fun <F> invoke(context: BootstrapContext<ForIO, F>) = client.updateAndGet {
            if (it != null) throw IllegalStateException("Discord client already created")
            DiscordClient(context)
        }.map { it!! }
    }

    override val webSocketStateHolder: WebSocketStateHolder = WebSocketStateHolderImpl()
    override val rest: RestClient<ForIO> get() = context.restClient.memoize().extract()
    override val token: String = context.gatewayContext.connectionContext.shardSettings.token

    private lateinit var gateway: Gateway<F>

    override val self: IdentifiedF<ForIO, ISelf> = token.verify(Either.monadError())
        .getOrHandle {
            error(it.message)
        } identify {
        rest.call { userService.getCurrentUser() }
    }

    override val users get() = cache.values.filterIsInstance<IUser>()
    override val guilds get() = cache.values.filterIsInstance<IGuild>()

    @ExperimentalCoroutinesApi
    @FlowPreview
    @Suppress("UNCHECKED_CAST")
    override fun login() {
        gateway = Gateway.create(context.gatewayContext)
        gateway.run()
    }

    override fun use(block: suspend ConcurrentSyntax<ForIO>.(IDiscordClient) -> Unit) = IO.fx {
        this.block(this@DiscordClient)
        logout()
    }

    override fun logout() {
        if (::gateway.isInitialized)
            gateway.close()
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

    override fun getInvite(code: String): IO<IInvite> = rest.call { inviteService.getInvite(code) }.fix()

    override fun deleteInvite(code: String, reason: String?) =
        rest.effect { inviteService.deleteInvite(code) }.fix()

    override fun getRegions(): IO<ListK<IRegion>> = rest.call(ListK.functor()) {
        voiceService.getVoiceRegions()
    }.map { it.fix() }

    override fun getChannel(id: Snowflake) = (cache[id] as IChannel?)?.just()
        ?: rest.call { channelService.getChannel(id) }.flatTap { cache[id] = it; just() }

    override fun getGuild(id: Snowflake) = guilds.find { it.id == id }.orNull()?.just()
        ?: rest.call { guildService.getGuild(id) }.flatTap { cache[id] = it; just() }

    override fun getUser(id: Snowflake) =
        users.find { it.id == id }.orNull()?.just() ?: rest.call { userService.getUser(id) }.flatTap {
            cache[id] = it; just()
        }

    override fun getMember(userId: Snowflake, guildId: Snowflake) =
        cache.values.filterIsInstance<IMember>()
            .filter { it.guild.id == guildId }
            .find { it.id == userId }.orNull()?.just() ?: getUser(userId).flatMap { it.asMember(guildId) }.flatTap {
            cache[userId] = it; just()
        }

    override fun getMessage(channelId: Snowflake, messageId: Snowflake): IO<IMessage> =
        cache.values.filterIsInstance<IMessage>()
            .filter { it.channel.id == channelId }
            .find { it.id == messageId }.orNull()?.just()
            ?: rest.call {
                channelService.getMessage(channelId, messageId)
            }.flatTap { cache += it.id to it; just() }
}
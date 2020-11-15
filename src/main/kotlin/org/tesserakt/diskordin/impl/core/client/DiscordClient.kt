package org.tesserakt.diskordin.impl.core.client

import arrow.core.ListK
import arrow.core.extensions.list.foldable.find
import arrow.core.extensions.listk.functor.functor
import arrow.core.fix
import arrow.core.left
import arrow.core.right
import arrow.fx.coroutines.ConcurrentVar
import arrow.fx.coroutines.Promise
import arrow.fx.coroutines.parTraverse
import arrow.fx.coroutines.stream.drain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.core.client.IDiscordClient
import org.tesserakt.diskordin.core.data.IdentifiedIO
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.`object`.IInvite
import org.tesserakt.diskordin.core.entity.`object`.IRegion
import org.tesserakt.diskordin.core.entity.builder.GuildCreateBuilder
import org.tesserakt.diskordin.gateway.Gateway
import org.tesserakt.diskordin.rest.RestClient
import org.tesserakt.diskordin.rest.call
import org.tesserakt.diskordin.util.DomainError

internal class DiscordClient private constructor(
    selfId: Snowflake,
    private val gateway: Gateway,
    internal val context: BootstrapContext
) : IDiscordClient {
    object AlreadyStarted : DomainError()

    companion object {
        internal val client = ConcurrentVar.unsafeEmpty<DiscordClient>()

        internal suspend operator fun invoke(selfId: Snowflake, gateway: Gateway, context: BootstrapContext) =
            if (client.isEmpty()) {
                client.put(DiscordClient(selfId, gateway, context))
                client.read().right()
            } else AlreadyStarted.left()
    }

    override val token: String = context.gatewayContext.connectionContext.shardSettings.token
    override val rest: RestClient get() = context.restClient

    private val logoutToken = Promise.unsafe<Unit>()

    override val self: IdentifiedIO<ISelf> = selfId.identify<ISelf> {
        rest.call { userService.getCurrentUser() }
    }

    override val users get() = cache.values.filterIsInstance<IUser>()
    override val guilds get() = cache.values.filterIsInstance<IGuild>()

    @ExperimentalCoroutinesApi
    override suspend fun login() {
        gateway.run().parTraverse { it.drain() }
    }

    @ExperimentalCoroutinesApi
    override suspend fun logout() {
        gateway.close()
        DiscordClient.client.tryTake()
        logoutToken.complete(Unit)
    }

    override suspend fun createGuild(name: String, builder: GuildCreateBuilder.() -> Unit): IGuild = rest.call {
        val inst = GuildCreateBuilder(name).apply(builder)
        guildService.createGuild(inst.create())
    }

    override suspend fun getInvite(code: String): IInvite = rest.call { inviteService.getInvite(code) }

    override suspend fun deleteInvite(code: String, reason: String?) =
        rest.effect { inviteService.deleteInvite(code) }

    override suspend fun getRegions(): ListK<IRegion> = rest.call(ListK.functor()) {
        voiceService.getVoiceRegions()
    }.fix()

    override suspend fun getChannel(id: Snowflake) = cache[id] as IChannel?
        ?: rest.call { channelService.getChannel(id) }.also { cache[id] = it }

    override suspend fun getGuild(id: Snowflake) = guilds.find { it.id == id }.orNull()
        ?: rest.call { guildService.getGuild(id) }.also { cache[id] = it }

    override suspend fun getGuildPreview(id: Snowflake): IGuildPreview = rest.call {
        guildService.getGuildPreview(id)
    }

    override suspend fun getUser(id: Snowflake) =
        users.find { it.id == id }.orNull() ?: rest.call { userService.getUser(id) }.also {
            cache[id] = it
        }

    override suspend fun getMember(userId: Snowflake, guildId: Snowflake) =
        cache.values.filterIsInstance<IMember>()
            .filter { it.guild.id == guildId }
            .find { it.id == userId }.orNull() ?: getUser(userId).asMember(guildId).also {
            cache[userId] = it
        }

    override suspend fun getMessage(channelId: Snowflake, messageId: Snowflake): IMessage =
        cache.values.filterIsInstance<IMessage>()
            .filter { it.channel.id == channelId }
            .find { it.id == messageId }.orNull()
            ?: rest.call {
                channelService.getMessage(channelId, messageId)
            }.also { cache += it.id to it; }
}
package org.tesserakt.diskordin.impl.core.client

import arrow.core.ListK
import arrow.core.extensions.listk.functor.functor
import arrow.core.fix
import arrow.core.left
import arrow.core.right
import arrow.fx.coroutines.ConcurrentVar
import arrow.fx.coroutines.parTraverse
import arrow.fx.coroutines.stream.drain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.core.client.IDiscordClient
import org.tesserakt.diskordin.core.client.ShardContext
import org.tesserakt.diskordin.core.data.IdentifiedIO
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.`object`.IInvite
import org.tesserakt.diskordin.core.entity.`object`.IRegion
import org.tesserakt.diskordin.core.entity.builder.GuildCreateBuilder
import org.tesserakt.diskordin.gateway.Gateway
import org.tesserakt.diskordin.rest.RestClient
import org.tesserakt.diskordin.rest.call
import org.tesserakt.diskordin.rest.callCaching
import org.tesserakt.diskordin.util.DomainError

internal class DiscordClient private constructor(
    selfId: Snowflake,
    private val gateway: Gateway,
    override val context: BootstrapContext
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

    override val token: String = context[ShardContext].token
    override val rest: RestClient get() = context[RestClient]

    override val self: IdentifiedIO<ISelf> = selfId.identify<ISelf> {
        rest.callCaching { userService.getCurrentUser() }
    }

    override val users get() = cacheSnapshot.users.values.toList()
    override val guilds get() = cacheSnapshot.guilds.values.toList()

    override suspend fun login() {
        gateway.run().parTraverse { it.drain() }
    }

    @ExperimentalCoroutinesApi
    override suspend fun logout() {
        gateway.close()
        DiscordClient.client.tryTake()
    }

    override suspend fun createGuild(name: String, builder: GuildCreateBuilder.() -> Unit): IGuild = rest.callCaching {
        val inst = GuildCreateBuilder(name).apply(builder)
        guildService.createGuild(inst.create())
    }

    override suspend fun getInvite(code: String): IInvite = rest.call { inviteService.getInvite(code) }

    override suspend fun deleteInvite(code: String, reason: String?) =
        rest.effect { inviteService.deleteInvite(code) }

    override suspend fun getRegions(): ListK<IRegion> = rest.call(ListK.functor()) {
        voiceService.getVoiceRegions()
    }.fix()

    override suspend fun getChannel(id: Snowflake) = cacheSnapshot.getChannel(id)
        ?: rest.callCaching { channelService.getChannel(id) }

    override suspend fun getGuild(id: Snowflake) = cacheSnapshot.getGuild(id)
        ?: rest.callCaching { guildService.getGuild(id) }

    override suspend fun getGuildPreview(id: Snowflake): IGuildPreview = rest.call {
        guildService.getGuildPreview(id)
    }

    override suspend fun getUser(id: Snowflake) =
        cacheSnapshot.getUser(id) ?: rest.callCaching { userService.getUser(id) }

    override suspend fun getMember(userId: Snowflake, guildId: Snowflake) =
        cacheSnapshot.getMember(guildId, userId) ?: rest.callCaching(guildId) {
            guildService.getMember(guildId, userId)
        }

    override suspend fun getMessage(channelId: Snowflake, messageId: Snowflake): IMessage =
        cacheSnapshot.getMessage(channelId, messageId) ?: rest.callCaching {
            channelService.getMessage(channelId, messageId)
        }
}
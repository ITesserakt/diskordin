package org.tesserakt.diskordin.impl.core.client

import arrow.core.left
import arrow.core.rightIfNotNull
import arrow.fx.coroutines.Atomic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.joinAll
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.core.client.IDiscordClient
import org.tesserakt.diskordin.core.client.ShardContext
import org.tesserakt.diskordin.core.data.DeferredIdentified
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.deferred
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.`object`.IInvite
import org.tesserakt.diskordin.core.entity.`object`.IRegion
import org.tesserakt.diskordin.core.entity.builder.GuildCreateBuilder
import org.tesserakt.diskordin.gateway.Gateway
import org.tesserakt.diskordin.rest.RestClient
import org.tesserakt.diskordin.rest.callCaching
import org.tesserakt.diskordin.util.DomainError

internal class DiscordClient private constructor(
    selfId: Snowflake,
    private val gateway: Gateway,
    override val context: BootstrapContext
) : IDiscordClient {
    object AlreadyStarted : DomainError()
    object NotInitialized : DomainError()

    companion object {
        private val client = Atomic.unsafe<IDiscordClient?>(null)

        suspend fun getInitialized() = client.get().rightIfNotNull { NotInitialized }

        suspend operator fun invoke(selfId: Snowflake, gateway: Gateway, context: BootstrapContext) =
            if (client.get() == null) {
                client.updateAndGet { DiscordClient(selfId, gateway, context) }.rightIfNotNull { NotInitialized }
            } else AlreadyStarted.left()

        internal suspend fun removeState() {
            client.set(null)
        }
    }

    override val token: String = context[ShardContext].token
    override val rest: RestClient get() = context[RestClient]

    override val self: DeferredIdentified<ISelf> = selfId deferred {
        rest.callCaching { userService.getCurrentUser() }
    }

    override val users get() = cacheSnapshot.users.values.toList()
    override val guilds get() = cacheSnapshot.guilds.values.toList()

    override suspend fun login() {
        gateway.run().joinAll()
    }

    @ExperimentalCoroutinesApi
    override suspend fun logout() {
        gateway.close()
        removeState()
    }

    override suspend fun createGuild(name: String, builder: GuildCreateBuilder.() -> Unit): IGuild = rest.callCaching {
        val inst = GuildCreateBuilder(name).apply(builder)
        guildService.createGuild(inst.create())
    }

    override suspend fun getInvite(code: String): IInvite = rest.call { inviteService.getInvite(code) }

    override suspend fun deleteInvite(code: String, reason: String?) =
        rest.effect { inviteService.deleteInvite(code) }

    override suspend fun getRegions(): List<IRegion> = rest.callRaw {
        voiceService.getVoiceRegions()
    }.map { it.unwrap() }

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
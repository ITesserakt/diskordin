package org.tesserakt.diskordin.impl.core.client

import arrow.core.*
import arrow.core.computations.either
import arrow.core.extensions.either.monad.mproduct
import arrow.core.extensions.either.monadError.monadError
import arrow.core.extensions.eval.applicative.just
import arrow.core.extensions.map.foldable.foldLeft
import kotlinx.coroutines.runBlocking
import org.tesserakt.diskordin.core.cache.*
import org.tesserakt.diskordin.core.client.*
import org.tesserakt.diskordin.core.data.EntityCache
import org.tesserakt.diskordin.core.data.EntitySifter
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.`object`.IGatewayStats
import org.tesserakt.diskordin.gateway.shard.Intents
import org.tesserakt.diskordin.gateway.shard.IntentsStrategy
import org.tesserakt.diskordin.impl.util.typeclass.integral
import org.tesserakt.diskordin.rest.RestClient
import org.tesserakt.diskordin.util.DomainError
import org.tesserakt.diskordin.util.enums.ValuedEnum
import kotlin.experimental.or

inline class BackendProvider<B : DiscordClientBuilderScope>(private val provider: () -> B) {
    suspend operator fun invoke(block: suspend B.() -> Unit) = DiscordClientBuilder.invoke(provider, block)
}

suspend infix fun <B : DiscordClientBuilderScope> BackendProvider<B>.configure(block: suspend B.() -> Unit) =
    invoke(block).getOrHandle { error(it) }

object DiscordClientBuilder {
    object NoTokenProvided : DomainError()

    @JvmStatic
    suspend operator fun <S : DiscordClientBuilderScope> invoke(
        instance: () -> S,
        block: suspend S.() -> Unit
    ): Either<DomainError, IDiscordClient> = either {
        val builder = instance().apply { block() }.create()
        val (globalContext, selfId) = formBootstrapContext(builder)()
        val gateway = builder.gatewayFactory.run { globalContext.createGateway() }

        DiscordClient(selfId, gateway, globalContext)()
    }

    @JvmStatic
    operator fun <B : DiscordClientBuilderScope> get(provider: BackendProvider<B>) = provider

    @JvmStatic
    infix fun <B : DiscordClientBuilderScope> by(provider: BackendProvider<B>) = provider

    private suspend fun formBootstrapContext(
        builder: DiscordClientBuilderScope.DiscordClientSettings
    ): Either<DomainError, Tuple2<BootstrapContext, Snowflake>> = either {
        val (token, selfId) = Either.fromNullable(System.getenv("DISKORDIN_TOKEN") ?: builder.token)
            .mapLeft { NoTokenProvided }.mproduct { it.verify(Either.monadError()) }()

        val gatewayStats = Eval.later {
            runBlocking { builder.restClient.call { gatewayService.getGatewayBot() } }
        }

        val intents: ValuedEnum<Intents, Short> = when (val strategy = builder.gatewaySettings.intents) {
            is IntentsStrategy.EnableOnly -> ValuedEnum(strategy.enabled.foldLeft(0, Short::or), Short.integral())
            else -> ValuedEnum.all(Short.integral())
        }

        val sifter = EntitySifter(intents)
        val processor = CacheProcessor(
            mapOf(
                IGuild::class to GuildUpdater,
                IRole::class to RoleUpdater,
                IUser::class to UserUpdater,
                IMember::class to MemberUpdater,
                IMessage::class to MessageUpdater,
                IChannel::class to ChannelUpdater
            ), mapOf(
                IGuild::class to GuildDeleter,
                IRole::class to RoleDeleter,
                IUser::class to UserDeleter,
                IMember::class to MemberDeleter,
                IMessage::class to MessageDeleter,
                IChannel::class to ChannelDeleter
            ), sifter
        )
        val shardContext = formShardSettings(token, builder, gatewayStats)
        val connectionContext = ConnectionContext(builder.gatewaySettings.url, builder.gatewaySettings.compression)
        val gatewayContext = GatewayContext(
            builder.gatewaySettings.coroutineContext,
            builder.gatewaySettings.interceptors + processor
        )

        BootstrapContext(
            mapOf(
                GatewayContext to gatewayContext,
                ConnectionContext to connectionContext,
                ShardContext to shardContext,
                RestClient to builder.restClient,
                EntityCache to EntityCache(EntitySifter(intents)),
                CacheProcessor to processor
            ) + builder.extensions
        ) toT selfId
    }

    private fun formShardSettings(
        token: String,
        builder: DiscordClientBuilderScope.DiscordClientSettings,
        gatewayInfo: Eval<IGatewayStats>
    ) = ShardContext(
        token,
        builder.gatewaySettings.shardCount.takeIf { it != 0 }?.just() ?: gatewayInfo.map { it.shards },
        builder.gatewaySettings.compressionStrategy,
        builder.gatewaySettings.guildSubscriptionsStrategy,
        builder.gatewaySettings.threshold,
        builder.gatewaySettings.initialPresence,
        builder.gatewaySettings.intents
    )
}
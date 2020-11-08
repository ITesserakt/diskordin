package org.tesserakt.diskordin.impl.core.client

import arrow.core.Either
import arrow.core.Eval
import arrow.core.computations.either
import arrow.core.extensions.either.monad.mproduct
import arrow.core.extensions.either.monadError.monadError
import arrow.core.extensions.eval.applicative.just
import kotlinx.coroutines.runBlocking
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.core.client.IDiscordClient
import org.tesserakt.diskordin.core.data.EntityCache
import org.tesserakt.diskordin.core.data.EntitySifter
import org.tesserakt.diskordin.core.entity.`object`.IGatewayStats
import org.tesserakt.diskordin.core.entity.builder.build
import org.tesserakt.diskordin.gateway.shard.Intents
import org.tesserakt.diskordin.gateway.shard.IntentsStrategy
import org.tesserakt.diskordin.impl.util.typeclass.integral
import org.tesserakt.diskordin.rest.RestClient
import org.tesserakt.diskordin.util.DomainError
import org.tesserakt.diskordin.util.enums.ValuedEnum
import org.tesserakt.diskordin.util.enums.or

inline class BackendProvider<B : DiscordClientBuilderScope>(private val provider: () -> B) {
    suspend operator fun invoke(block: B.() -> Unit) = DiscordClientBuilder.invoke(provider, block)
}

object DiscordClientBuilder {
    object NoTokenProvided : DomainError()

    @JvmStatic
    suspend operator fun <S : DiscordClientBuilderScope> invoke(
        instance: () -> S,
        block: S.() -> Unit
    ): Either<DomainError, IDiscordClient> = either {
        val builder = block.build(instance)
        val (token, selfId) = Either.fromNullable(System.getenv("DISKORDIN_TOKEN") ?: builder.token)
            .mapLeft { NoTokenProvided }.mproduct { it.verify(Either.monadError()) }()

        val gatewayStats = Eval.later(builder::restClient).map {
            runBlocking { it.call { gatewayService.getGatewayBot() } }
        }.memoize()

        val shardContext = formShardSettings(token, builder, gatewayStats)
        val connectionContext = formConnectionSettings(builder, shardContext)
        val gatewayContext = formGatewaySettings(builder, connectionContext)
        val globalContext = formBootstrapContext(builder, builder.restClient, gatewayContext)
        val gateway = builder.gatewayFactory.run { gatewayContext.createGateway() }

        DiscordClient(selfId, gateway, globalContext)()
    }

    @JvmStatic
    operator fun <B : DiscordClientBuilderScope> get(provider: BackendProvider<B>) = provider

    private fun formBootstrapContext(
        builder: DiscordClientBuilderScope.DiscordClientSettings,
        rest: RestClient,
        gatewayContext: BootstrapContext.Gateway
    ): BootstrapContext {
        val strategy = builder.gatewaySettings.intents

        val intents: ValuedEnum<Intents, Short> = if (strategy is IntentsStrategy.EnableOnly)
            strategy.enabled
                .map { (_, code) -> ValuedEnum<Intents, Short>(code, Short.integral()) }
                .fold(ValuedEnum.none(Short.integral())) { acc, i -> acc or i }
        else {
            ValuedEnum.all(Short.integral())
        }

        return BootstrapContext(
            EntityCache(EntitySifter(intents), builder.cache),
            rest,
            gatewayContext
        )
    }

    private fun formGatewaySettings(
        builder: DiscordClientBuilderScope.DiscordClientSettings,
        connectionContext: BootstrapContext.Gateway.Connection
    ): BootstrapContext.Gateway = BootstrapContext.Gateway(
        builder.gatewaySettings.coroutineContext,
        builder.gatewaySettings.interceptors,
        connectionContext
    )

    private fun formConnectionSettings(
        builder: DiscordClientBuilderScope.DiscordClientSettings,
        shardSettings: BootstrapContext.Gateway.Connection.ShardSettings
    ): BootstrapContext.Gateway.Connection = BootstrapContext.Gateway.Connection(
        "wss://gateway.discord.gg",
        builder.gatewaySettings.compression,
        shardSettings
    )

    private fun formShardSettings(
        token: String,
        builder: DiscordClientBuilderScope.DiscordClientSettings,
        gatewayInfo: Eval<IGatewayStats>
    ): BootstrapContext.Gateway.Connection.ShardSettings = BootstrapContext.Gateway.Connection.ShardSettings(
        token,
        builder.gatewaySettings.shardCount.takeIf { it != 0 }?.just() ?: gatewayInfo.map { it.shards },
        builder.gatewaySettings.compressionStrategy,
        builder.gatewaySettings.guildSubscriptionsStrategy,
        builder.gatewaySettings.threshold,
        builder.gatewaySettings.initialPresence,
        builder.gatewaySettings.intents
    )
}
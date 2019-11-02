package ru.tesserakt.diskordin.impl.core.client

import arrow.core.Id
import arrow.core.ListK
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.functor.functor
import arrow.core.extensions.listk.functor.functor
import arrow.core.fix
import arrow.core.handleLeftWith
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.fix
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.inject
import ru.tesserakt.diskordin.core.client.EventDispatcher
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.client.TokenType
import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.combine
import ru.tesserakt.diskordin.core.data.event.UserUpdateEvent
import ru.tesserakt.diskordin.core.data.event.guild.GuildCreateEvent
import ru.tesserakt.diskordin.core.entity.IChannel
import ru.tesserakt.diskordin.core.entity.IGuild
import ru.tesserakt.diskordin.core.entity.ISelf
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.core.entity.`object`.IInvite
import ru.tesserakt.diskordin.core.entity.`object`.IRegion
import ru.tesserakt.diskordin.core.entity.builder.GuildCreateBuilder
import ru.tesserakt.diskordin.core.entity.builder.build
import ru.tesserakt.diskordin.gateway.Gateway
import ru.tesserakt.diskordin.impl.core.client.TokenVerification.VerificationError.*
import ru.tesserakt.diskordin.rest.RestClient
import ru.tesserakt.diskordin.rest.call
import ru.tesserakt.diskordin.util.Loggers
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime

data class DiscordClient(
    override val tokenType: TokenType
) : IDiscordClient {
    @ExperimentalCoroutinesApi
    override lateinit var eventDispatcher: EventDispatcher
    private val logger by Loggers
    override val token: String = getKoin().getProperty("token")!!
    override val rest: RestClient<ForIO> by inject()

    init {
        TokenVerification(token, tokenType).verify().map { id ->
            self = id combine {
                rest.call(Id.functor()) {
                    userService.getCurrentUser()
                }.fix().suspended().extract()
            }
            logger.info("Token verified")
        }.handleLeftWith {
            val message = when (it) {
                BlankString -> "Token cannot be blank"
                CorruptedId -> "Token is corrupted!"
                InvalidCharacters -> "Token contains invalid characters!"
                InvalidConstruction -> "Token does not fit into right form!"
            }
            throw IllegalStateException(message)
        }
    }

    override lateinit var self: Identified<ISelf>
        private set

    override var isConnected: Boolean = false
        private set

    @ExperimentalCoroutinesApi
    override lateinit var gateway: Gateway
        private set

    override lateinit var users: Flow<IUser>
        private set
    override lateinit var guilds: Flow<IGuild>
        private set

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    override suspend fun login() = IO.fx {
        val gatewayStats = rest.call(Id.functor()) { gatewayService.getGatewayBot() }.bind().extract()
        val gatewayURL = gatewayStats.url
        val metadata = gatewayStats.session
        this@DiscordClient.gateway = Gateway(gatewayURL, metadata.total, metadata.remaining, metadata.resetAfter)
        eventDispatcher = gateway.eventDispatcher
        isConnected = true

        guilds = eventDispatcher.subscribeOn<GuildCreateEvent>()
            .map { it.guild }
        users = eventDispatcher.subscribeOn<UserUpdateEvent>()
            .map { it.user() }

        this@DiscordClient.gateway.run()
        Unit
    }.unsafeRunSync()

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    override suspend fun use(block: suspend IDiscordClient.() -> Unit) {
        isConnected = true
        block()
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

    override suspend fun findUser(id: Snowflake) = rest.call(Id.functor()) {
        userService.getUser(id)
    }.fix().attempt().suspended().toOption().orNull()?.extract()

    override suspend fun findGuild(id: Snowflake) = rest.call(Id.functor()) {
        guildService.getGuild(id)
    }.fix().attempt().suspended().toOption().orNull()?.extract()

    override suspend fun findChannel(id: Snowflake) = rest.call(Id.functor()) {
        channelService.getChannel<IChannel>(id)
    }.fix().attempt().suspended().toOption().orNull()?.extract()

    override suspend fun createGuild(request: GuildCreateBuilder.() -> Unit) = rest.call(Id.functor()) {
        guildService.createGuild(request.build())
    }.fix().suspended().extract()

    override suspend fun getInvite(code: String): IInvite = rest.call(Id.functor()) {
        inviteService.getInvite(code)
    }.fix().suspended().extract()

    override suspend fun deleteInvite(code: String, reason: String?) = rest.effect {
        inviteService.deleteInvite(code)
    }.fix().suspended()

    override suspend fun getRegions(): List<IRegion> = rest.call(ListK.functor()) {
        voiceService.getVoiceRegions()
    }.fix().suspended().fix()

    override suspend fun getChannel(id: Snowflake): IChannel = findChannel(id)!!

    override suspend fun getGuild(id: Snowflake): IGuild = findGuild(id)!!

    override suspend fun getUser(id: Snowflake): IUser = findUser(id)!!
}
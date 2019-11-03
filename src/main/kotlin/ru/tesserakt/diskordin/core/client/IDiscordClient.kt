package ru.tesserakt.diskordin.core.client

import arrow.core.ListK
import arrow.fx.ForIO
import arrow.fx.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.`object`.IInvite
import ru.tesserakt.diskordin.core.entity.`object`.IRegion
import ru.tesserakt.diskordin.core.entity.builder.GuildCreateBuilder
import ru.tesserakt.diskordin.gateway.Gateway
import ru.tesserakt.diskordin.rest.RestClient

interface IDiscordClient : IDiscordObject {
    val eventDispatcher: EventDispatcher
    val token: String
    val tokenType: TokenType
    val self: Identified<ISelf>
    val isConnected: Boolean
    @ExperimentalCoroutinesApi
    val gateway: Gateway
    val rest: RestClient<ForIO>

    /*
    Performs a login to discord servers and enables the Gateway
     */
    fun login(): IO<Unit>

    /*
    Should be used when need fast connect to Discord. Does not runs the Gateway
     */
    suspend fun use(block: suspend IDiscordClient.() -> Unit)

    fun logout()
    fun getUser(id: Snowflake): IO<IUser>
    fun getGuild(id: Snowflake): IO<IGuild>
    fun getChannel(id: Snowflake): IO<IChannel>
    fun createGuild(request: GuildCreateBuilder.() -> Unit): IO<IGuild>
    fun getInvite(code: String): IO<IInvite>
    fun deleteInvite(code: String, reason: String?): IO<Unit>
    fun getRegions(): IO<ListK<IRegion>>

    val users: IO<List<IUser>>
    val guilds: IO<List<IGuild>>
}

enum class TokenType {
    Bot, Bearer, Webhook
}

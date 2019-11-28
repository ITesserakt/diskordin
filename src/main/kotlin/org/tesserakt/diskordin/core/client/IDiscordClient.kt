package org.tesserakt.diskordin.core.client

import arrow.core.ListK
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.rx2.ForFlowableK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.tesserakt.diskordin.core.data.Identified
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.`object`.IInvite
import org.tesserakt.diskordin.core.entity.`object`.IRegion
import org.tesserakt.diskordin.core.entity.builder.GuildCreateBuilder
import org.tesserakt.diskordin.gateway.Gateway
import org.tesserakt.diskordin.rest.RestClient

interface IDiscordClient : IDiscordObject {
    val eventDispatcher: EventDispatcher<ForFlowableK>
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
    fun login()

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

package org.tesserakt.diskordin.core.client

import arrow.core.ListK
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.rx2.ForFlowableK
import arrow.fx.typeclasses.ConcurrentSyntax
import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.`object`.IInvite
import org.tesserakt.diskordin.core.entity.`object`.IRegion
import org.tesserakt.diskordin.core.entity.builder.GuildCreateBuilder
import org.tesserakt.diskordin.gateway.Gateway
import org.tesserakt.diskordin.rest.RestClient

interface IDiscordClient : IDiscordObject {
    val eventDispatcher: EventDispatcher<ForFlowableK>
    val webSocketStateHolder: WebSocketStateHolder
    val token: String
    val self: IdentifiedF<ForIO, ISelf>
    val isConnected: Boolean
    val gateway: Gateway
    val rest: RestClient<ForIO>

    /*
    Performs a login to discord servers and enables the Gateway
     */
    fun login(): IO<Unit>

    /*
    Should be used when need fast connect to Discord. Does not runs the Gateway
     */
    fun use(block: suspend ConcurrentSyntax<ForIO>.(IDiscordClient) -> Unit): IO<Unit>

    fun logout()
    fun getUser(id: Snowflake): IO<IUser>
    fun getGuild(id: Snowflake): IO<IGuild>
    fun getChannel(id: Snowflake): IO<IChannel>
    fun getMember(userId: Snowflake, guildId: Snowflake): IO<IMember>
    fun createGuild(
        name: String,
        region: IRegion,
        icon: String,
        verificationLevel: IGuild.VerificationLevel,
        defaultMessageNotificationLevel: IGuild.DefaultMessageNotificationLevel,
        explicitContentFilter: IGuild.ExplicitContentFilter,
        builder: GuildCreateBuilder.() -> Unit
    ): IO<IGuild>

    fun getInvite(code: String): IO<IInvite>
    fun deleteInvite(code: String, reason: String?): IO<Unit>
    fun getRegions(): IO<ListK<IRegion>>
    fun getMessage(channelId: Snowflake, messageId: Snowflake): IO<IMessage>

    val users: List<IUser>
    val guilds: List<IGuild>
}

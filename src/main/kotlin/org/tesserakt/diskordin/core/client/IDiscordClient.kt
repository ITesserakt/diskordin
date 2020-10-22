package org.tesserakt.diskordin.core.client

import arrow.core.ListK
import arrow.fx.ForIO
import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.`object`.IInvite
import org.tesserakt.diskordin.core.entity.`object`.IRegion
import org.tesserakt.diskordin.core.entity.builder.GuildCreateBuilder
import org.tesserakt.diskordin.rest.RestClient

interface IDiscordClient : IDiscordObject {
    val webSocketStateHolder: WebSocketStateHolder
    val token: String
    val self: IdentifiedF<ForIO, ISelf>
    val rest: RestClient

    /*
    Performs a login to discord servers and enables the Gateway
     */
    suspend fun login()

    suspend fun logout()
    suspend fun getUser(id: Snowflake): IUser
    suspend fun getGuild(id: Snowflake): IGuild
    suspend fun getChannel(id: Snowflake): IChannel
    suspend fun getMember(userId: Snowflake, guildId: Snowflake): IMember
    suspend fun createGuild(
        name: String,
        region: IRegion,
        icon: String,
        verificationLevel: IGuild.VerificationLevel,
        defaultMessageNotificationLevel: IGuild.DefaultMessageNotificationLevel,
        explicitContentFilter: IGuild.ExplicitContentFilter,
        builder: GuildCreateBuilder.() -> Unit
    ): IGuild

    suspend fun getInvite(code: String): IInvite
    suspend fun deleteInvite(code: String, reason: String?)
    suspend fun getRegions(): ListK<IRegion>
    suspend fun getMessage(channelId: Snowflake, messageId: Snowflake): IMessage

    val users: List<IUser>
    val guilds: List<IGuild>
}

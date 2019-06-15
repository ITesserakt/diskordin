@file:Suppress("unused")

package ru.tesserakt.diskordin.impl.core.service

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.builder.DMCreateBuilder
import ru.tesserakt.diskordin.core.entity.builder.UserEditBuilder
import ru.tesserakt.diskordin.core.entity.builder.build
import ru.tesserakt.diskordin.core.entity.query.UserGuildsQuery
import ru.tesserakt.diskordin.core.entity.query.build
import ru.tesserakt.diskordin.impl.core.cache.genericCache
import ru.tesserakt.diskordin.impl.core.entity.Connection
import ru.tesserakt.diskordin.impl.core.entity.Self
import ru.tesserakt.diskordin.impl.core.entity.User
import ru.tesserakt.diskordin.impl.core.rest.resource.UserResource

internal object UserService {
    private val userCache = genericCache<IUser>()

    suspend fun getCurrentUser(): IUser = User(UserResource.General.getCurrentUser())

    suspend fun getUser(userId: Snowflake): IUser? = runCatching {
        UserResource.General.getUser(userId.asLong())
    }.map { User(it) }.getOrNull()

    suspend fun editCurrentUser(builder: UserEditBuilder.() -> Unit): ISelf =
        Self(UserResource.General.editCurrentUser(builder.build()))

    suspend fun getCurrentUserGuilds(query: UserGuildsQuery.() -> Unit) = UserResource.Guilds
        .getCurrentUserGuilds(query.build<UserGuildsQuery>())
        .mapNotNull { GuildService.getGuild(it.id.asSnowflake()) }

    suspend fun leaveGuild(guildId: Snowflake) =
        UserResource.Guilds.leaveGuild(guildId.asLong())

    suspend fun getCurrentUserPrivateChannels() =
        UserResource.Channels.getUserPrivateChannels()
            .map { IChannel.typed<IPrivateChannel>(it) }

    suspend fun joinIntoDM(builder: DMCreateBuilder.() -> Unit) = //TODO: make group dm
        UserResource.Channels.joinPrivateChannel(builder.build())
            .let { IChannel.typed<IChannel>(it) }

    suspend fun getCurrentUserConnections(): List<IConnection> =
        UserResource.Connections.getCurrentUserConnections()
            .map { Connection(it) }
}
@file:Suppress("unused")

package ru.tesserakt.diskordin.impl.core.service

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.builder.DMCreateBuilder
import ru.tesserakt.diskordin.core.entity.builder.UserEditBuilder
import ru.tesserakt.diskordin.core.entity.builder.build
import ru.tesserakt.diskordin.core.entity.query.UserGuildsQuery
import ru.tesserakt.diskordin.core.entity.query.query
import ru.tesserakt.diskordin.impl.core.entity.Connection
import ru.tesserakt.diskordin.impl.core.entity.GroupPrivateChannel
import ru.tesserakt.diskordin.impl.core.entity.Self
import ru.tesserakt.diskordin.impl.core.entity.User
import ru.tesserakt.diskordin.rest.resource.UserResource

internal object UserService {
    //private val userCache = genericCache<IUser>()

    suspend fun getCurrentUser(): ISelf = Self(UserResource.General.getCurrentUser())

    suspend fun getUser(userId: Snowflake): IUser? = runCatching {
        UserResource.General.getUser(userId.asLong())
    }.map { User(it) }.getOrNull()

    suspend fun editCurrentUser(builder: UserEditBuilder.() -> Unit): ISelf =
        Self(UserResource.General.editCurrentUser(builder.build()))

    suspend fun getCurrentUserGuilds(query: UserGuildsQuery.() -> Unit) = UserResource.Guilds
        .getCurrentUserGuilds(query.query())
        .mapNotNull { GuildService.getGuild(it.id.asSnowflake()) }

    suspend fun leaveGuild(guildId: Snowflake) =
        UserResource.Guilds.leaveGuild(guildId.asLong())

    suspend fun getCurrentUserPrivateChannels() =
        UserResource.Channels.getUserPrivateChannels()
            .map { IChannel.typed<IPrivateChannel>(it) }

    suspend fun joinIntoDM(builder: DMCreateBuilder.() -> Unit) =
        UserResource.Channels.joinPrivateChannel(builder.build())
            .let { IChannel.typed<GroupPrivateChannel>(it) }

    suspend fun getCurrentUserConnections(): List<IConnection> =
        UserResource.Connections.getCurrentUserConnections()
            .map { Connection(it) }
}
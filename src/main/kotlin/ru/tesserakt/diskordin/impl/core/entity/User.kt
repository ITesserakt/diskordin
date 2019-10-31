package ru.tesserakt.diskordin.impl.core.entity


import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.response.UserGuildResponse
import ru.tesserakt.diskordin.core.data.json.response.UserResponse
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.builder.DMCreateBuilder
import ru.tesserakt.diskordin.core.entity.builder.UserEditBuilder
import ru.tesserakt.diskordin.core.entity.builder.build
import ru.tesserakt.diskordin.core.entity.query.UserGuildsQuery
import ru.tesserakt.diskordin.util.enums.ValuedEnum
import ru.tesserakt.diskordin.util.typeclass.integral

open class User(raw: UserResponse<IUser>) : IUser {
    final override val avatar: String? = raw.avatar

    final override val mfaEnabled: Boolean = raw.mfa_enabled ?: false

    final override val locale: String? = raw.locale

    final override val verified: Boolean = raw.verified ?: false

    final override val email: String? = raw.email

    final override val flags: ValuedEnum<IUser.Flags, Short> = ValuedEnum(raw.flags ?: 0, Short.integral())

    final override val premiumType: IUser.Type? = IUser.Type.values().find { it.ordinal == raw.premium_type }

    final override val username: String = raw.username

    final override val discriminator: Short = raw.discriminator.toShort()

    final override val isBot: Boolean = raw.bot ?: false

    final override val id: Snowflake = raw.id

    override suspend fun asMember(guildId: Snowflake): IMember =
        client.getGuild(guildId).members.first { it.id == id }

    override val mention: String = "<@$id>"
}

class Self(raw: UserResponse<ISelf>) : User(raw), ISelf {
    override val guilds: Flow<UserGuildResponse> = flow {
        userService.getCurrentUserGuilds(UserGuildsQuery().apply {
            this.limit = 1000
        }.create()).forEach { emit(it) }
    }

    override val privateChannels: Flow<IPrivateChannel> = flow {
        userService.getUserDMs().map { it.unwrap() }.forEach { emit(it) }
    }

    override val connections: Flow<IConnection> = flow {
        userService.getCurrentUserConnections().map { it.unwrap() }.forEach { emit(it) }
    }

    override suspend fun leaveGuild(guild: IGuild) = leaveGuild(guild.id)

    override suspend fun leaveGuild(guildId: Snowflake) = userService.leaveGuild(guildId)

    override suspend fun joinIntoDM(builder: DMCreateBuilder.() -> Unit): IChannel =
        userService.joinToDM(builder.build()).unwrap()

    override suspend fun edit(builder: UserEditBuilder.() -> Unit): ISelf =
        userService.editCurrentUser(builder.build()).unwrap()
}
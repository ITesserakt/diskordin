package ru.tesserakt.diskordin.impl.core.entity


import arrow.core.Id
import arrow.core.ListK
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.functor.functor
import arrow.core.extensions.listk.functor.functor
import arrow.core.fix
import arrow.fx.fix
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.response.UserResponse
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.builder.DMCreateBuilder
import ru.tesserakt.diskordin.core.entity.builder.UserEditBuilder
import ru.tesserakt.diskordin.core.entity.builder.build
import ru.tesserakt.diskordin.core.entity.query.UserGuildsQuery
import ru.tesserakt.diskordin.rest.call
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

    override fun toString(): String {
        return "User(avatar=$avatar, mfaEnabled=$mfaEnabled, locale=$locale, verified=$verified, email=$email, flags=$flags, premiumType=$premiumType, username='$username', discriminator=$discriminator, isBot=$isBot, id=$id, mention='$mention')"
    }

    override val mention: String = "<@${id.asString()}>"
}

class Self(raw: UserResponse<ISelf>) : User(raw), ISelf {
    override val guilds: Flow<IGuild> = flow {
        rest.call(ListK.functor()) {
            userService.getCurrentUserGuilds(UserGuildsQuery().create())
        }.fix().suspended().fix().forEach { emit(it) }
    }

    override val privateChannels: Flow<IPrivateChannel> = flow {
        rest.call(ListK.functor()) {
            userService.getUserDMs()
        }.fix().suspended().fix().forEach { emit(it) }
    }

    override val connections: Flow<IConnection> = flow {
        rest.call(ListK.functor()) {
            userService.getCurrentUserConnections()
        }.fix().suspended().fix().forEach { emit(it) }
    }

    override suspend fun leaveGuild(guild: IGuild) = leaveGuild(guild.id)

    override suspend fun leaveGuild(guildId: Snowflake) = rest.effect {
        userService.leaveGuild(guildId)
    }.fix().suspended()

    override suspend fun joinIntoDM(builder: DMCreateBuilder.() -> Unit): IChannel = rest.call(Id.functor()) {
        userService.joinToDM(builder.build())
    }.fix().suspended().extract()

    override suspend fun edit(builder: UserEditBuilder.() -> Unit): ISelf = rest.call(Id.functor()) {
        userService.editCurrentUser(builder.build())
    }.fix().suspended().extract()

    override fun toString(): String {
        return "Self(guilds=$guilds, privateChannels=$privateChannels, connections=$connections) ${super.toString()}"
    }
}
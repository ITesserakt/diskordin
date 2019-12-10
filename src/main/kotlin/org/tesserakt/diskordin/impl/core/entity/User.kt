package org.tesserakt.diskordin.impl.core.entity


import arrow.core.Id
import arrow.core.ListK
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.functor.functor
import arrow.core.extensions.listk.functor.functor
import arrow.core.fix
import arrow.fx.IO
import arrow.fx.extensions.io.monad.map
import arrow.fx.fix
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.UserResponse
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.builder.DMCreateBuilder
import org.tesserakt.diskordin.core.entity.builder.UserEditBuilder
import org.tesserakt.diskordin.core.entity.builder.build
import org.tesserakt.diskordin.core.entity.query.UserGuildsQuery
import org.tesserakt.diskordin.impl.util.typeclass.integral
import org.tesserakt.diskordin.rest.call
import org.tesserakt.diskordin.util.enums.ValuedEnum

internal open class User(raw: UserResponse<IUser>) : IUser {
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

    override fun asMember(guildId: Snowflake): IO<IMember> = rest.call(guildId, Id.functor()) {
        guildService.getMember(guildId, id)
    }.map { it.extract() }

    override fun toString(): String {
        return "User(" +
                "avatar=$avatar, " +
                "mfaEnabled=$mfaEnabled, " +
                "locale=$locale, " +
                "verified=$verified, " +
                "email=$email, " +
                "flags=$flags, " +
                "premiumType=$premiumType, " +
                "username='$username', " +
                "discriminator=$discriminator, " +
                "isBot=$isBot, " +
                "id=$id, " +
                "mention='$mention'" +
                ")"
    }

    override val mention: String = "<@${id.asString()}>"
}

internal class Self(raw: UserResponse<ISelf>) : User(raw), ISelf {
    override fun joinIntoDM(to: Snowflake): IO<IPrivateChannel> = rest.call {
        userService.joinToDM(DMCreateBuilder().apply {
            recipientId = to
        }.create())
    }.fix()

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

    override fun leaveGuild(guild: IGuild) = leaveGuild(guild.id)

    override fun leaveGuild(guildId: Snowflake) = rest.effect {
        userService.leaveGuild(guildId)
    }.fix()

    override fun edit(builder: UserEditBuilder.() -> Unit): IO<ISelf> = rest.call(Id.functor()) {
        userService.editCurrentUser(builder.build())
    }.map { it.extract() }

    override fun toString(): String {
        return StringBuilder("Self(")
            .appendln("guilds=$guilds, ")
            .appendln("privateChannels=$privateChannels, ")
            .appendln("connections=$connections")
            .appendln(") ${super.toString()}")
            .toString()
    }
}
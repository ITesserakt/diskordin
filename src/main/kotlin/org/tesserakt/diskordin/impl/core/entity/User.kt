package org.tesserakt.diskordin.impl.core.entity


import arrow.core.Id
import arrow.core.ListK
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.functor.functor
import arrow.core.extensions.listk.functor.functor
import arrow.core.fix
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

    final override val flags: ValuedEnum<IUser.Flags, Int> = if (raw.flags == null || raw.flags == 0)
        ValuedEnum.none(Int.integral())
    else ValuedEnum(raw.flags, Int.integral())

    final override val premiumType: IUser.Type? = IUser.Type.values().find { it.ordinal == raw.premium_type }

    final override val username: String = raw.username

    final override val discriminator: Short = raw.discriminator.toShort()

    final override val isBot: Boolean = raw.bot ?: false

    final override val id: Snowflake = raw.id

    override suspend fun asMember(guildId: Snowflake): IMember = rest.call(guildId, Id.functor()) {
        guildService.getMember(guildId, id)
    }.extract()

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
    override suspend fun joinIntoDM(to: Snowflake): IPrivateChannel = rest.call {
        userService.joinToDM(DMCreateBuilder().apply {
            recipientId = to
        }.create())
    }

    override val guilds = flow {
        rest.call(ListK.functor()) {
            userService.getCurrentUserGuilds(UserGuildsQuery().create())
        }.fix().forEach { emit(it) }
    }

    override val privateChannels = flow {
        rest.call(ListK.functor()) {
            userService.getUserDMs()
        }.fix().forEach { emit(it) }
    }

    override val connections = flow {
        rest.call(ListK.functor()) {
            userService.getCurrentUserConnections()
        }.fix().forEach { emit(it) }
    }

    override suspend fun leaveGuild(guild: IGuild) = leaveGuild(guild.id)

    override suspend fun leaveGuild(guildId: Snowflake) = rest.effect {
        userService.leaveGuild(guildId)
    }

    override suspend fun edit(builder: UserEditBuilder.() -> Unit): ISelf = rest.call(Id.functor()) {
        userService.editCurrentUser(builder.build(::UserEditBuilder))
    }.extract()

    override fun toString(): String {
        return StringBuilder("Self(")
            .appendLine("guilds=$guilds, ")
            .appendLine("privateChannels=$privateChannels, ")
            .appendLine("connections=$connections")
            .appendLine(") ${super.toString()}")
            .toString()
    }
}
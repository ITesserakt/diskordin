package org.tesserakt.diskordin.impl.core.entity


import arrow.core.Id
import arrow.core.extensions.id.applicative.just
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.functor.functor
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.UserResponse
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.builder.DMCreateBuilder
import org.tesserakt.diskordin.core.entity.builder.UserEditBuilder
import org.tesserakt.diskordin.core.entity.builder.build
import org.tesserakt.diskordin.core.entity.query.UserGuildsQuery
import org.tesserakt.diskordin.impl.util.typeclass.integral
import org.tesserakt.diskordin.rest.call
import org.tesserakt.diskordin.rest.stream
import org.tesserakt.diskordin.util.enums.ValuedEnum

internal open class User(raw: UserResponse<IUser>) : IUser {
    final override val avatar: String? = raw.avatar

    final override val mfaEnabled: Boolean = raw.mfa_enabled ?: false

    final override val locale: String? = raw.locale

    final override val verified: Boolean = raw.verified ?: false

    final override val email: String? = raw.email

    final override val flags: ValuedEnum<IUser.Flags, Int> = ValuedEnum(raw.flags ?: 0, Int.integral())

    final override val premiumType: IUser.Type? = IUser.Type.values().find { it.ordinal == raw.premium_type }

    final override val username: String = raw.username

    final override val discriminator: Short = raw.discriminator.toShort()

    final override val isBot: Boolean = raw.bot ?: false

    final override val id: Snowflake = raw.id

    override suspend fun asMember(guildId: Snowflake): IMember = rest.call(guildId, Id.functor()) {
        guildService.getMember(guildId, id).just()
    }.extract()

    override fun toString(): String {
        return "User(avatar=$avatar, mfaEnabled=$mfaEnabled, locale=$locale, verified=$verified, email=$email, flags=$flags, premiumType=$premiumType, username='$username', discriminator=$discriminator, isBot=$isBot, id=$id, mention='$mention')"
    }

    override val mention: String = "<@${id.asString()}>"
}

internal class Self(raw: UserResponse<ISelf>) : User(raw), ISelf {
    override suspend fun joinIntoDM(to: Snowflake): IPrivateChannel = rest.call {
        userService.joinToDM(DMCreateBuilder().apply {
            recipientId = to
        }.create())
    }

    override val guilds = rest.stream {
        userService.getCurrentUserGuilds(UserGuildsQuery().create())
    }

    override val privateChannels = rest.stream {
        userService.getUserDMs()
    }

    override val connections = rest.stream {
        userService.getCurrentUserConnections()
    }

    override suspend fun leaveGuild(guild: IGuild) = leaveGuild(guild.id)

    override suspend fun leaveGuild(guildId: Snowflake) = rest.effect {
        userService.leaveGuild(guildId)
    }

    override suspend fun edit(builder: UserEditBuilder.() -> Unit): ISelf = rest.call {
        userService.editCurrentUser(builder.build(::UserEditBuilder))
    }

    override fun toString(): String {
        return "Self(guilds=$guilds, privateChannels=$privateChannels, connections=$connections) " +
                "\n   ${super.toString()}"
    }
}
package ru.tesserakt.diskordin.impl.core.entity

import arrow.core.getOrElse
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.GuildMemberResponse
import ru.tesserakt.diskordin.core.entity.IGuild
import ru.tesserakt.diskordin.core.entity.IMember
import ru.tesserakt.diskordin.core.entity.IRole
import ru.tesserakt.diskordin.util.Identified
import java.time.Instant
import java.time.format.DateTimeFormatter

class Member constructor(
    raw: GuildMemberResponse,
    guildId: Snowflake,
    override val kodein: Kodein
) : IMember {
    override val client: IDiscordClient by instance()

    override val nickname: String? = raw.nick


    @FlowPreview
    override val roles: Flow<IRole> = flow {
        raw.roles.map(Snowflake.Companion::of)
            .map { guild.extract().findRole(it).getOrElse { throw IllegalArgumentException() } }
            .forEach { emit(it) }
    }

    override val joinTime: Instant = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(raw.joinedAt, Instant::from)

    override val username: String = raw.user.username

    override val discriminator: Short = raw.user.discriminator.toShort()

    override val isBot: Boolean = raw.user.bot ?: false


    override val id: Snowflake = raw.user.id.asSnowflake()


    override val guild: Identified<IGuild> = Identified(guildId) {
        client.findGuild(it).getOrElse { throw IllegalArgumentException("guildId is not right") }
    }


    override val mention: String = "<@!$id>"
}
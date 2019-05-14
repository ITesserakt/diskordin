package ru.tesserakt.diskordin.impl.core.entity

import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.data.Permission
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.computePermissions
import ru.tesserakt.diskordin.core.data.json.response.RoleResponse
import ru.tesserakt.diskordin.core.entity.IGuild
import ru.tesserakt.diskordin.core.entity.IRole
import ru.tesserakt.diskordin.util.Identified
import java.awt.Color
import java.util.*
import kotlin.NoSuchElementException

class Role constructor(
    raw: RoleResponse,
    guildId: Snowflake,
    override val kodein: Kodein
) : IRole {
    @ExperimentalUnsignedTypes
    override val permissions: EnumSet<Permission> = raw.permissions.computePermissions()

    override val color: Color = Color(raw.color)

    override val isHoisted: Boolean = raw.hoist

    override val isMentionable: Boolean = raw.mentionable

    override val id: Snowflake = raw.id.asSnowflake()

    override val client: IDiscordClient by instance()

    override val guild: Identified<IGuild> = Identified(guildId) {
        client.findGuild(it) ?: throw NoSuchElementException("Неверный id гильдии")
    }

    override val mention: String = "<@&$id>"

    override val name: String = raw.name

    override suspend fun delete(reason: String?) {
        TODO("not implemented")
    }
}

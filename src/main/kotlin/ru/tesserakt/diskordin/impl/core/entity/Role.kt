package ru.tesserakt.diskordin.impl.core.entity


import ru.tesserakt.diskordin.core.data.Permission
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.RoleResponse
import ru.tesserakt.diskordin.core.entity.IGuild
import ru.tesserakt.diskordin.core.entity.IRole
import ru.tesserakt.diskordin.core.entity.builder.RoleEditBuilder
import ru.tesserakt.diskordin.impl.core.service.GuildService
import ru.tesserakt.diskordin.rest.resource.GuildResource
import ru.tesserakt.diskordin.util.Identified
import ru.tesserakt.diskordin.util.enums.ValuedEnum
import java.awt.Color

class Role constructor(
    raw: RoleResponse,
    private val guildId: Snowflake
) : IRole {
    override suspend fun edit(builder: RoleEditBuilder.() -> Unit): IRole =
        GuildService.editRole(guildId, id, builder)

    @ExperimentalUnsignedTypes
    override val permissions = ValuedEnum<Permission>(raw.permissions)

    override val color: Color = Color(raw.color)

    override val isHoisted: Boolean = raw.hoist

    override val isMentionable: Boolean = raw.mentionable

    override val id: Snowflake = raw.id.asSnowflake()

    override val isEveryone: Boolean = id == guildId

    override val guild: Identified<IGuild> = Identified(guildId) {
        client.findGuild(it) ?: throw NoSuchElementException("Guild id is not right")
    }

    override val mention: String = "<@&$id>"

    override val name: String = raw.name

    override suspend fun delete(reason: String?) =
        GuildResource.Roles.deleteRole(guildId.asLong(), id.asLong(), reason)
}
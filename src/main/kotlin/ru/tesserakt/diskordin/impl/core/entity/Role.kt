package ru.tesserakt.diskordin.impl.core.entity


import arrow.core.Id
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.functor.functor
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.map
import arrow.fx.fix
import ru.tesserakt.diskordin.core.data.Permission
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.identify
import ru.tesserakt.diskordin.core.data.json.response.RoleResponse
import ru.tesserakt.diskordin.core.entity.IRole
import ru.tesserakt.diskordin.core.entity.builder.RoleEditBuilder
import ru.tesserakt.diskordin.core.entity.builder.instance
import ru.tesserakt.diskordin.core.entity.client
import ru.tesserakt.diskordin.core.entity.rest
import ru.tesserakt.diskordin.rest.call
import ru.tesserakt.diskordin.util.enums.ValuedEnum
import ru.tesserakt.diskordin.util.typeclass.integral
import java.awt.Color

class Role constructor(
    raw: RoleResponse,
    guildId: Snowflake
) : IRole {
    override fun edit(builder: RoleEditBuilder.() -> Unit): IO<IRole> = rest.call(guild.id, Id.functor()) {
        val inst = builder.instance()
        guildService.editRole(guild.id, id, inst.create(), inst.reason)
    }.map { it.extract() }

    @ExperimentalUnsignedTypes
    override val permissions = ValuedEnum<Permission, Long>(raw.permissions, Long.integral())

    override val color: Color = Color(raw.color)

    override val isHoisted: Boolean = raw.hoist

    override val isMentionable: Boolean = raw.mentionable

    override val id: Snowflake = raw.id

    override val isEveryone: Boolean = id == guildId

    override val guild = guildId identify {
        client.getGuild(it).bind()
    }

    override val mention: String = "<@&$id>"

    override val name: String = raw.name

    override fun delete(reason: String?) = rest.effect {
        guildService.deleteRole(guild.id, id, reason)
    }.fix()

    @ExperimentalUnsignedTypes
    override fun toString(): String {
        return "Role(permissions=$permissions, color=$color, isHoisted=$isHoisted, isMentionable=$isMentionable, id=$id, isEveryone=$isEveryone, guild=$guild, mention='$mention', name='$name')"
    }
}

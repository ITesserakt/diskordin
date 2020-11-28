package org.tesserakt.diskordin.impl.core.entity


import arrow.core.Id
import arrow.core.extensions.id.applicative.just
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.functor.functor
import org.tesserakt.diskordin.core.data.Permission
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.RoleResponse
import org.tesserakt.diskordin.core.data.json.response.UnwrapContext
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.builder.RoleEditBuilder
import org.tesserakt.diskordin.core.entity.builder.instance
import org.tesserakt.diskordin.impl.util.typeclass.integral
import org.tesserakt.diskordin.rest.call
import org.tesserakt.diskordin.util.enums.ValuedEnum
import java.awt.Color

internal class Role constructor(
    override val raw: RoleResponse,
    guildId: Snowflake
) : IRole, ICacheable<IRole, UnwrapContext.GuildContext, RoleResponse> {
    override suspend fun edit(builder: RoleEditBuilder.() -> Unit) = rest.call(guild.id, Id.functor()) {
        val inst = builder.instance(::RoleEditBuilder)
        guildService.editRole(guild.id, id, inst.create(), inst.reason).just()
    }.extract()

    override val permissions = ValuedEnum<Permission, Long>(raw.permissions, Long.integral())

    override val color: Color = Color(raw.color)

    override val isHoisted: Boolean = raw.hoist

    override val isMentionable: Boolean = raw.mentionable

    override val id: Snowflake = raw.id

    override val isEveryone: Boolean = id == guildId

    override val guild = guildId.identify<IGuild> {
        client.getGuild(it)
    }

    override val mention: String = "<@&$id>"

    override val name: String = raw.name

    override suspend fun delete(reason: String?) = rest.effect {
        guildService.deleteRole(guild.id, id, reason)
    }

    @ExperimentalUnsignedTypes
    override fun toString(): String {
        return "Role(permissions=$permissions, color=$color, isHoisted=$isHoisted, isMentionable=$isMentionable, id=$id, isEveryone=$isEveryone, guild=$guild, mention='$mention', name='$name')"
    }

    override fun fromCache(): IRole = cache[id] as IRole

    override fun copy(changes: (RoleResponse) -> RoleResponse): IRole = raw.let(changes).unwrap(guild.id)
}

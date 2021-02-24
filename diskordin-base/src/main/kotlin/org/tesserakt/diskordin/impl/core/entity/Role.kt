@file:Suppress("DEPRECATION")

package org.tesserakt.diskordin.impl.core.entity


import org.tesserakt.diskordin.core.data.Permission
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.deferred
import org.tesserakt.diskordin.core.data.json.response.RoleResponse
import org.tesserakt.diskordin.core.data.json.response.UnwrapContext
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.ICacheable
import org.tesserakt.diskordin.core.entity.IRole
import org.tesserakt.diskordin.core.entity.builder.RoleEditBuilder
import org.tesserakt.diskordin.core.entity.builder.instance
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.core.entity.rest
import org.tesserakt.diskordin.impl.util.typeclass.integral
import org.tesserakt.diskordin.util.enums.ValuedEnum
import java.awt.Color

class Role constructor(
    override val raw: RoleResponse,
    guildId: Snowflake
) : IRole, ICacheable<IRole, UnwrapContext.GuildContext, RoleResponse> {
    @Suppress("CANDIDATE_CHOSEN_USING_OVERLOAD_RESOLUTION_BY_LAMBDA_ANNOTATION")
    override suspend fun edit(builder: RoleEditBuilder.() -> Unit) = rest.callRaw {
        val inst = builder.instance(::RoleEditBuilder)
        guildService.editRole(guild.id, id, inst.create(), inst.reason)
    }.unwrap(guild.id)

    override val permissions = ValuedEnum<Permission, Long>(raw.permissions, Long.integral())

    override val color: Color = Color(raw.color)

    override val isHoisted: Boolean = raw.hoist

    override val isMentionable: Boolean = raw.mentionable

    override val id: Snowflake = raw.id

    override val isEveryone: Boolean = id == guildId

    override val guild = guildId deferred {
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

    override fun copy(changes: (RoleResponse) -> RoleResponse): IRole = raw.let(changes).unwrap(guild.id)
}

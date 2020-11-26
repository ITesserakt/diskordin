package org.tesserakt.diskordin.core.data.json.response

import arrow.optics.Lens
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IRole
import org.tesserakt.diskordin.impl.core.entity.Role

data class RoleResponse(
    val id: Snowflake,
    val name: String,
    val color: Int,
    val hoist: Boolean,
    val position: Int,
    val permissions: Long,
    val managed: Boolean,
    val mentionable: Boolean
) : DiscordResponse<IRole, UnwrapContext.GuildContext>() {
    override fun unwrap(ctx: UnwrapContext.GuildContext): IRole = Role(this, ctx.guildId)

    companion object {
        val id: Lens<RoleResponse, Snowflake> = Lens(
            { it.id }, { it, new -> it.copy(id = new) }
        )

        val name: Lens<RoleResponse, String> = Lens(
            { it.name }, { it, new -> it.copy(name = new) }
        )

        val color: Lens<RoleResponse, Int> = Lens(
            { it.color }, { it, new -> it.copy(color = new) }
        )

        val hoist: Lens<RoleResponse, Boolean> = Lens(
            { it.hoist }, { it, new -> it.copy(hoist = new) }
        )

        val position: Lens<RoleResponse, Int> = Lens(
            { it.position }, { it, new -> it.copy(position = new) }
        )

        val permissions: Lens<RoleResponse, Long> = Lens(
            { it.permissions }, { it, new -> it.copy(permissions = new) }
        )

        val managed: Lens<RoleResponse, Boolean> = Lens(
            { it.managed }, { it, new -> it.copy(managed = new) }
        )

        val mentionable: Lens<RoleResponse, Boolean> = Lens(
            { it.mentionable }, { it, new -> it.copy(mentionable = new) }
        )
    }
}
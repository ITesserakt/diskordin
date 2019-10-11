package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.impl.core.entity.Role


data class RoleResponse(
    val id: Long,
    val name: String,
    val color: Int,
    val hoist: Boolean,
    val position: Int,
    val permissions: Long,
    val managed: Boolean,
    val mentionable: Boolean
) : DiscordResponse() {
    fun unwrap(guildId: Snowflake) = Role(this, guildId)
}
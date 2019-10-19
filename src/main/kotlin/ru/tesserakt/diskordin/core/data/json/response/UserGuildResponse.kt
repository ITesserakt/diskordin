package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.IGuild
import ru.tesserakt.diskordin.impl.core.entity.PartialGuild

data class UserGuildResponse(
    val id: Snowflake,
    val name: String,
    val icon: String,
    val owner: Boolean,
    val permissions: Int
) : DiscordResponse<IGuild>() {
    override fun unwrap(vararg params: Any) = PartialGuild(this)
}
package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.`object`.IPermissionOverwrite
import ru.tesserakt.diskordin.impl.core.entity.`object`.PermissionOverwrite


data class OverwriteResponse(
    val id: Snowflake,
    val type: String,
    val allow: Long,
    val deny: Long
) : DiscordResponse<IPermissionOverwrite>() {
    override fun unwrap(vararg params: Any): IPermissionOverwrite = PermissionOverwrite(this)
}
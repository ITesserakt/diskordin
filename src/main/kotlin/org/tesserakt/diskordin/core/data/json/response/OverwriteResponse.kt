package org.tesserakt.diskordin.core.data.json.response

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.`object`.IPermissionOverwrite
import org.tesserakt.diskordin.impl.core.entity.`object`.PermissionOverwrite


data class OverwriteResponse(
    val id: Snowflake,
    val type: String,
    val allow: Long,
    val deny: Long
) : DiscordResponse<IPermissionOverwrite, UnwrapContext.EmptyContext>() {
    override fun unwrap(ctx: UnwrapContext.EmptyContext): IPermissionOverwrite = PermissionOverwrite(this)
}
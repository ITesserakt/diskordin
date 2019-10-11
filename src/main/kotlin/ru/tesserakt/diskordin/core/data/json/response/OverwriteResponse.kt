package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.impl.core.entity.`object`.PermissionOverwrite


data class OverwriteResponse(
    val id: Long,
    val type: String,
    val allow: Long,
    val deny: Long
) : DiscordResponse() {
    fun unwrap() = PermissionOverwrite(this)
}
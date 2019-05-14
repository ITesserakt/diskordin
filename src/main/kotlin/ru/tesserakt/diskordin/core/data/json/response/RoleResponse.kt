package ru.tesserakt.diskordin.core.data.json.response


data class RoleResponse(
    val id: Long,
    val name: String,
    val color: Int,
    val hoist: Boolean,
    val position: Int,
    val permissions: Long,
    val managed: Boolean,
    val mentionable: Boolean
) : DiscordResponse()
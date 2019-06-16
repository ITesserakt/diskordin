package ru.tesserakt.diskordin.core.data.json.request


data class GuildRoleEditRequest(
    val name: String? = null,
    val permissions: Long? = null,
    val color: Int? = null,
    val hoist: Boolean? = null,
    val mentionable: Boolean? = null
) : JsonRequest()

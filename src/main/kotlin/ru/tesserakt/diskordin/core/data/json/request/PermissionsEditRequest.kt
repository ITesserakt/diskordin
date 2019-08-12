package ru.tesserakt.diskordin.core.data.json.request


data class PermissionsEditRequest(
    val allow: Long,
    val deny: Long,
    val type: String
) : JsonRequest()

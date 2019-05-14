package ru.tesserakt.diskordin.core.data.json.request


data class PermissionsEditRequest(
    val allow: Int,
    val deny: Int,
    val type: String
) : JsonRequest()

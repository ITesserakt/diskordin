package ru.tesserakt.diskordin.core.data.json.response


data class VoiceRegionResponse(
    val id: String,
    val name: String,
    val vip: Boolean,
    val optimal: Boolean,
    val deprecated: Boolean,
    val custom: Boolean
) : DiscordResponse()

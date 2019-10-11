package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.impl.core.entity.`object`.Region


data class VoiceRegionResponse(
    val id: String,
    val name: String,
    val vip: Boolean,
    val optimal: Boolean,
    val deprecated: Boolean,
    val custom: Boolean
) : DiscordResponse() {
    fun unwrap() = Region(this)
}

package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.impl.core.entity.`object`.GuildEmbed


data class GuildEmbedResponse(
    val enabled: Boolean,
    val channel_id: Long?
) : DiscordResponse() {
    fun unwrap() = GuildEmbed(this)
}

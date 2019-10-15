package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.core.entity.`object`.IGuildEmbed
import ru.tesserakt.diskordin.impl.core.entity.`object`.GuildEmbed


data class GuildEmbedResponse(
    val enabled: Boolean,
    val channel_id: Long?
) : DiscordResponse<IGuildEmbed>() {
    override fun unwrap(vararg params: Any): IGuildEmbed = GuildEmbed(this)
}

package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.impl.core.entity.`object`.Reaction


data class ReactionResponse(
    val count: Int,
    val me: Boolean,
    val emoji: EmojiResponse
) : DiscordResponse() {
    fun unwrap() = Reaction(this)
}

package ru.tesserakt.diskordin.core.data.json.response


data class ReactionResponse(
    val count: Int,
    val me: Boolean,
    val emoji: EmojiResponse
) : DiscordResponse()

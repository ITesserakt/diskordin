package ru.tesserakt.diskordin.core.data.json.response

import ru.tesserakt.diskordin.core.entity.IDiscordObject

/**
 * Marks class as a raw response from Discord
 */
abstract class DiscordResponse<out T : IDiscordObject> {
    abstract fun unwrap(vararg params: Any): T
}
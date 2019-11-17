package org.tesserakt.diskordin.core.data.json.response

import arrow.core.Option
import arrow.core.none
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IDiscordObject

/**
 * Marks class as a raw response from Discord
 */
abstract class DiscordResponse<out T : IDiscordObject, in C : UnwrapContext> {
    abstract fun unwrap(ctx: C): T
}

fun <T : IDiscordObject> DiscordResponse<T, UnwrapContext.EmptyContext>.unwrap() =
    this.unwrap(UnwrapContext.EmptyContext)

fun <T : IDiscordObject> DiscordResponse<T, UnwrapContext.GuildContext>.unwrap(guildId: Snowflake) =
    unwrap(UnwrapContext.GuildContext(guildId))

fun <T : IDiscordObject> DiscordResponse<T, UnwrapContext.PartialGuildContext>.unwrap(guildId: Option<Snowflake> = none()) =
    unwrap(UnwrapContext.PartialGuildContext(guildId.orNull()))

sealed class UnwrapContext {
    object EmptyContext : UnwrapContext()
    data class GuildContext(val guildId: Snowflake) : UnwrapContext()
    data class PartialGuildContext(val guildId: Snowflake?) : UnwrapContext()
}
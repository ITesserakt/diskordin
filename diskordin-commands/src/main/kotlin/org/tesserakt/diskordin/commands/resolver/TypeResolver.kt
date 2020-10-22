package org.tesserakt.diskordin.commands.resolver

import arrow.core.Either
import arrow.core.rightIfNotNull
import org.tesserakt.diskordin.commands.CommandContext
import org.tesserakt.diskordin.core.entity.IMentioned
import org.tesserakt.diskordin.core.entity.StaticMention

interface TypeResolver<T : Any, in C : CommandContext> {
    suspend fun parse(context: C, input: String): Either<ParseError, T>
}

interface MentionableTypeResolver<T : IMentioned, in C : CommandContext> : TypeResolver<T, C> {
    data class NotFoundError(val input: String) : ParseError("Could not find any object by '$input'")

    suspend fun parseByMention(context: C, input: String): T?
    suspend fun parseById(context: C, input: String): T?
    suspend fun rawParse(context: C, input: String): T?
    val static: StaticMention<T, *>

    override suspend fun parse(context: C, input: String) = when {
        static.mention.matches(input) -> parseByMention(context, input)
        Regex("""\d{18,}""").matches(input) -> parseById(context, input)
        else -> rawParse(context, input)
    }.rightIfNotNull { NotFoundError(input) }
}


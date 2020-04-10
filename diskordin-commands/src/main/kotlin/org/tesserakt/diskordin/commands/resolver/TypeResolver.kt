package org.tesserakt.diskordin.commands.resolver

import arrow.mtl.EitherT
import arrow.mtl.OptionT
import arrow.typeclasses.Functor
import org.tesserakt.diskordin.commands.CommandContext
import org.tesserakt.diskordin.core.entity.IMentioned
import org.tesserakt.diskordin.core.entity.StaticMention

interface TypeResolver<T : Any, F, in C : CommandContext<F>> {
    fun parse(context: C, input: String): EitherT<out ParseError, F, T>
}

interface MentionableTypeResolver<T : IMentioned, F, in C : CommandContext<F>> : TypeResolver<T, F, C>, Functor<F> {
    data class NotFoundError(val input: String) : ParseError("Could not find any object by '$input'")

    fun parseByMention(context: C, input: String): OptionT<F, T>
    fun parseById(context: C, input: String): OptionT<F, T>
    fun rawParse(context: C, input: String): OptionT<F, T>
    val static: StaticMention<T, *>

    override fun parse(context: C, input: String) = when {
        static.mention.matches(input) -> parseByMention(context, input)
        Regex("""\d{18,}""").matches(input) -> parseById(context, input)
        else -> rawParse(context, input)
    }.toRight(this) { NotFoundError(input) }
}


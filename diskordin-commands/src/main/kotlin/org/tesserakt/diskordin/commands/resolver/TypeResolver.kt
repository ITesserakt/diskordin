package org.tesserakt.diskordin.commands.resolver

import arrow.mtl.EitherT
import org.tesserakt.diskordin.commands.CommandContext

interface TypeResolver<T : Any, F, in C : CommandContext<F>> {
    fun parse(context: C, input: String): EitherT<out ParseError, F, T>
}


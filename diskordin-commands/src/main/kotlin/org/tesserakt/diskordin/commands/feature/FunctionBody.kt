package org.tesserakt.diskordin.commands.feature

import arrow.Kind
import arrow.core.Either
import arrow.core.Nel
import arrow.typeclasses.ApplicativeError
import io.github.classgraph.TypeSignature
import org.tesserakt.diskordin.commands.CommandContext
import org.tesserakt.diskordin.commands.CommandModule
import org.tesserakt.diskordin.commands.ValidationError

data class FunctionBody(
    val moduleType: TypeSignature,
    val contextType: TypeSignature,
    val call: (parent: Any, params: List<Any?>) -> Any
) : PersistentFeature<FunctionBody> {
    override fun <G> validate(AE: ApplicativeError<G, Nel<ValidationError>>): Kind<G, FunctionBody> = AE.just(this)

    @Suppress("UNCHECKED_CAST")
    suspend operator fun <F, C : CommandContext<F>> invoke(
        module: CommandModule<F, C>,
        ctx: C,
        rest: List<Any?> = emptyList()
    ) = Either.catch {
        call(module, rest + ctx) as Kind<F, Unit> //here we assume that function is a valid command
    }
}
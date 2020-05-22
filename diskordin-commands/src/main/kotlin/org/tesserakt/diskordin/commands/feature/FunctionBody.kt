package org.tesserakt.diskordin.commands.feature

import arrow.Kind
import arrow.core.Nel
import arrow.core.extensions.either.monad.flatten
import arrow.core.extensions.either.traverse.sequence
import arrow.core.left
import arrow.core.nonFatalOrThrow
import arrow.core.right
import arrow.fx.typeclasses.Async
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

    operator fun <F, C : CommandContext<F>> invoke(
        A: Async<F>,
        module: CommandModule<F, C>,
        ctx: C,
        rest: List<Any?> = emptyList()
    ) = A.run {
        try {
            call<Kind<F, *>>(module, rest + ctx).right() //here we assume that function is a valid command
        } catch (t: Throwable) {
            t.nonFatalOrThrow().left()
        }.map { it.attempt() }.sequence(A).map { it.flatten() }
    }

    private inline fun <reified T> call(parent: Any, params: List<Any?>) = call.invoke(parent, params) as T
}
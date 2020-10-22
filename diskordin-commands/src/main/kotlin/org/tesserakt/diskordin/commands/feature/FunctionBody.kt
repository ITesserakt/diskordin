package org.tesserakt.diskordin.commands.feature

import arrow.Kind
import arrow.core.Either
import arrow.core.Nel
import arrow.typeclasses.ApplicativeError
import org.tesserakt.diskordin.commands.CommandContext
import org.tesserakt.diskordin.commands.CommandModule
import org.tesserakt.diskordin.commands.ValidationError
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend

data class FunctionBody(
    val moduleType: KClass<CommandModule<*>>,
    val function: KFunction<Unit>
) : PersistentFeature<FunctionBody> {
    override fun <G> validate(AE: ApplicativeError<G, Nel<ValidationError>>): Kind<G, FunctionBody> = AE.just(this)

    suspend operator fun <C : CommandContext> invoke(
        module: CommandModule<C>,
        ctx: C,
        rest: List<Any?> = emptyList()
    ) = Either.catch {
        function.callSuspend(module, ctx, *rest.toTypedArray()) //here we assume that function is a valid command
    }
}
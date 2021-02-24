package org.tesserakt.diskordin.commands.feature

import arrow.core.Either
import arrow.core.ValidatedNel
import arrow.core.validNel
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
    override fun validate(): ValidatedNel<ValidationError, FunctionBody> = validNel()

    suspend operator fun <C : CommandContext> invoke(
        module: CommandModule<C>,
        ctx: C,
        rest: List<Any?> = emptyList()
    ) = Either.catch {
        function.callSuspend(module, ctx, *rest.toTypedArray()) //here we assume that function is a valid command
    }
}
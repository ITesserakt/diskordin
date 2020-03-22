package org.tesserakt.diskordin.commands.feature

import arrow.Kind
import arrow.core.Nel
import arrow.core.nel
import arrow.typeclasses.ApplicativeError
import io.github.classgraph.ClassRefTypeSignature
import io.github.classgraph.TypeSignature
import org.tesserakt.diskordin.commands.CommandContext
import org.tesserakt.diskordin.commands.Feature
import org.tesserakt.diskordin.commands.ValidationError
import kotlin.reflect.KClass

private const val COMMAND_CONTEXT = "org.tesserakt.diskordin.commands.CommandContext"

data class FunctionParameters(
    val commandName: String,
    val contextType: TypeSignature,
    val parameters: List<TypeSignature>
) : Feature<FunctionParameters> {
    data class DuplicatedParameters(val commandName: String, val parameterNames: List<TypeSignature>) :
        ValidationError("$commandName has duplicated next parameters: $parameterNames")

    data class MissedParameters(val commandName: String, val parameters: List<KClass<*>>) :
        ValidationError("$commandName should contain next parameters: ${parameters.map { it.simpleName }}")

    data class NonCoincidingContext(
        val commandName: String,
        val actualContextType: TypeSignature,
        val declaredContextType: TypeSignature
    ) : ValidationError("Context of $commandName should be equal with declared as type parameter in CommandModule. Actual: $actualContextType, declared: $declaredContextType")

    private fun ClassRefTypeSignature.subtypeOf(className: String) =
        if (this.classInfo == null) this.loadClass().isAssignableFrom(Class.forName(className))
        else classInfo.implementsInterface(className) or classInfo.extendsSuperclass(className)

    private fun <G> ClassRefTypeSignature.compareWithTypeBound(AE: ApplicativeError<G, Nel<ValidationError>>) =
        if (this == contextType) AE.just(this)
        else AE.raiseError(NonCoincidingContext(commandName, this, contextType).nel())

    private fun <G> List<TypeSignature>.validateContextParameter(AE: ApplicativeError<G, Nel<ValidationError>>) =
        AE.run {
            val classRefParams = filterIsInstance<ClassRefTypeSignature>()
            val contextParams = classRefParams.filter { it.subtypeOf(COMMAND_CONTEXT) }

            when (contextParams.size) {
                1 -> contextParams.first().compareWithTypeBound(AE)
                0 -> raiseError(MissedParameters(commandName, listOf(CommandContext::class)).nel())
                else -> raiseError(DuplicatedParameters(commandName, contextParams).nel())
            }
        }

    override fun <G> validate(AE: ApplicativeError<G, Nel<ValidationError>>): Kind<G, FunctionParameters> = AE.run {
        parameters.validateContextParameter(AE).map { this@FunctionParameters }
    }
}
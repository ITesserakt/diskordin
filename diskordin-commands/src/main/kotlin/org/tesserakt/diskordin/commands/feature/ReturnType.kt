package org.tesserakt.diskordin.commands.feature

import arrow.Kind
import arrow.core.*
import arrow.core.extensions.either.applicativeError.applicativeError
import arrow.typeclasses.ApplicativeError
import io.github.classgraph.ClassRefTypeSignature
import io.github.classgraph.ReferenceTypeSignature
import io.github.classgraph.TypeSignature
import org.tesserakt.diskordin.commands.ValidationError
import org.tesserakt.diskordin.commands.integration.logger

data class ReturnType(
    val commandName: String,
    val returnType: TypeSignature,
    val moduleType: ClassRefTypeSignature
) : PersistentFeature<ReturnType> {
    data class InvalidReturnType(val commandName: String, val actual: TypeSignature) :
        ValidationError("Return type of $commandName doesn't match with Kind<F, Unit>. Actual: $actual")

    data class UnresolvedType(val type: TypeSignature) :
        ValidationError("$type doesn't resolved. Try rescan including this class")

    data class DifferentReturnType(val commandName: String, val type: TypeSignature) :
        ValidationError("Return type of command $commandName doesn't equal to type defined in CommandModule")

    private fun <G> ClassRefTypeSignature.validateReturnType(AE: ApplicativeError<G, Nel<ValidationError>>) = AE.run {
        val kind = this@validateReturnType
        if (kind.typeArguments[1].typeSignature.toString() == "kotlin.Unit")
            kind.just()
        else raiseError(InvalidReturnType(commandName, kind).nel())
    }

    private fun <G> TypeSignature.asKinded(AE: ApplicativeError<G, Nel<ValidationError>>) = AE.run {
        if (this@asKinded !is ClassRefTypeSignature) return@run raiseError<ReferenceTypeSignature>(
            InvalidReturnType(commandName, this@asKinded).nel()
        )

        val type = this@asKinded
        return@run when {
            type.classInfo == null -> raiseError(UnresolvedType(type).nel())

            type.baseClassName == "arrow.Kind" -> type.validateReturnType(AE)

            type.classInfo.implementsInterface("arrow.Kind") -> {
                logger.log("Unchecked return type detected for command $commandName. Consider using Kind<F, Unit> against F<Unit>")
                type.classInfo.typeSignature.superinterfaceSignatures.first { it.baseClassName == "arrow.Kind" }.just()
            }

            else -> raiseError(InvalidReturnType(commandName, type).nel())
        }
    }

    override fun <G> validate(AE: ApplicativeError<G, Nel<ValidationError>>): Kind<G, ReturnType> = AE.run {
        val kind = returnType.asKinded(Either.applicativeError()).fix()
        val moduleDataClass = moduleType.typeArguments[0]

        return kind.flatMap {
            if (it is ClassRefTypeSignature && it.typeArguments[0] == moduleDataClass)
                ReturnType(commandName, it, moduleType).right()
            else
                DifferentReturnType(commandName, it).nel().left()
        }.fromEither(::identity)
    }
}

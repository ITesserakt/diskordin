package org.tesserakt.diskordin.commands.feature

import arrow.core.ValidatedNel
import arrow.core.invalidNel
import arrow.core.validNel
import io.github.classgraph.ClassRefTypeSignature
import io.github.classgraph.MethodParameterInfo
import org.tesserakt.diskordin.commands.ValidationError
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType

data class FunctionParameters(
    val commandName: String,
    val function: KFunction<*>,
    val moduleType: KType,
    val parameters: List<KParameter>,
    private val _parameters: List<MethodParameterInfo>,
    private val _moduleContext: ClassRefTypeSignature
) : PersistentFeature<FunctionParameters> {
    data class DuplicatedParameters(val commandName: String, val parameterNames: List<KParameter>) :
        ValidationError("$commandName has duplicated these parameters: $parameterNames")

    data class MissedParameters(val commandName: String, val contextName: String, val parameters: List<KParameter>) :
        ValidationError("$commandName should contain `$contextName`, but has these parameters: $parameters")

    private fun ClassRefTypeSignature.isSuperClassOf(type: ClassRefTypeSignature) =
        loadClass().isAssignableFrom(type.loadClass())

    override fun validate(): ValidatedNel<ValidationError, FunctionParameters> {
        val contextParams = _parameters.map { it.typeSignatureOrTypeDescriptor }
            .filterIsInstance<ClassRefTypeSignature>()
            .filter { it == _moduleContext || it.isSuperClassOf(_moduleContext) }

        return when (contextParams.size) {
            1 -> this@FunctionParameters.validNel()
            0 -> MissedParameters(commandName, _moduleContext.toStringWithSimpleNames(), parameters).invalidNel()
            else -> DuplicatedParameters(commandName, parameters).invalidNel()
        }
    }

    override fun toString(): String {
        return "FunctionParameters(commandName='$commandName', function=$function, moduleType=$moduleType, parameters=$parameters)"
    }
}
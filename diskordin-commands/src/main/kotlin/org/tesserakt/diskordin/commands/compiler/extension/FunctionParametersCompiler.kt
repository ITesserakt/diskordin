package org.tesserakt.diskordin.commands.compiler.extension

import io.github.classgraph.MethodInfo
import org.tesserakt.diskordin.commands.compiler.CompilerExtension
import org.tesserakt.diskordin.commands.feature.FunctionParameters

class FunctionParametersCompiler : CompilerExtension<FunctionParameters>() {
    override fun compileFeature(function: MethodInfo, name: String): FunctionParameters {
        val parameters = function.parameterInfo

        return FunctionParameters(
            name,
            function.classInfo.typeSignature.superclassSignature.typeArguments[1].typeSignature,
            parameters.map { it.typeSignatureOrTypeDescriptor }
        )
    }
}
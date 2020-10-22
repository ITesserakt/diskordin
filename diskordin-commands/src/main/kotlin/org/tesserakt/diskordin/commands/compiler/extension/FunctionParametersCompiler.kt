package org.tesserakt.diskordin.commands.compiler.extension

import io.github.classgraph.ClassRefTypeSignature
import io.github.classgraph.MethodInfo
import org.tesserakt.diskordin.commands.compiler.PersistentCompilerExtension
import org.tesserakt.diskordin.commands.feature.FunctionParameters
import org.tesserakt.diskordin.commands.integration.logger
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.kotlinFunction

class FunctionParametersCompiler : PersistentCompilerExtension<FunctionParameters>() {
    override fun compileFeature(function: MethodInfo, name: String): FunctionParameters {
        val method = function.loadClassAndGetMethod().kotlinFunction
            ?: throw NotKotlinFunction(function.loadClassAndGetMethod())
        val parameters = method.parameters

        if (!method.isSuspend)
            logger.log {
                "Function $name isn't suspending. Computations with side-effects should mark with suspend"
            }

        return FunctionParameters(
            name,
            method,
            parameters.first { it.kind == KParameter.Kind.INSTANCE }.type,
            method.parameters.filter { it.kind != KParameter.Kind.INSTANCE },
            function.parameterInfo.toList(),
            function.classInfo.typeSignature.superclassSignature.typeArguments[0].typeSignature as ClassRefTypeSignature
        )
    }
}
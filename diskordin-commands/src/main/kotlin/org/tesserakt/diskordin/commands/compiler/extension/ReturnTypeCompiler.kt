package org.tesserakt.diskordin.commands.compiler.extension

import io.github.classgraph.MethodInfo
import org.tesserakt.diskordin.commands.compiler.PersistentCompilerExtension
import org.tesserakt.diskordin.commands.feature.ReturnType
import kotlin.reflect.jvm.kotlinFunction

class ReturnTypeCompiler : PersistentCompilerExtension<ReturnType>() {
    override fun compileFeature(function: MethodInfo, name: String): ReturnType {
        val method = function.loadClassAndGetMethod().kotlinFunction
            ?: throw NotKotlinFunction(function.loadClassAndGetMethod())

        return ReturnType(name, method.returnType)
    }
}
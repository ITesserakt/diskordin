package org.tesserakt.diskordin.commands.compiler.extension

import io.github.classgraph.MethodInfo
import org.tesserakt.diskordin.commands.CommandModule
import org.tesserakt.diskordin.commands.compiler.PersistentCompilerExtension
import org.tesserakt.diskordin.commands.feature.FunctionBody
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.kotlinFunction

class FunctionBodyCompiler : PersistentCompilerExtension<FunctionBody>() {
    override fun compileFeature(function: MethodInfo, name: String): FunctionBody {
        val method = function.loadClassAndGetMethod().kotlinFunction as? KFunction<Unit>
            ?: throw NotKotlinFunction(function.loadClassAndGetMethod())

        val moduleType = function.classInfo.typeSignature.superclassSignature

        @Suppress("UNCHECKED_CAST")
        return FunctionBody(moduleType.loadClass().kotlin as KClass<CommandModule<*>>, method)
    }
}
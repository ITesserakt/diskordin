package org.tesserakt.diskordin.commands.compiler.extension

import io.github.classgraph.MethodInfo
import org.tesserakt.diskordin.commands.compiler.PersistentCompilerExtension
import org.tesserakt.diskordin.commands.feature.FunctionBody

class FunctionBodyCompiler : PersistentCompilerExtension<FunctionBody>() {
    override fun compileFeature(function: MethodInfo, name: String): FunctionBody {
        val method = function.loadClassAndGetMethod()
        val moduleType = function.classInfo.typeSignature.superclassSignature
        val contextType = moduleType.typeArguments[1].typeSignature

        return FunctionBody(moduleType, contextType) { parent: Any, params: List<Any?> ->
            method.invoke(parent, *params.toTypedArray())
        }
    }
}
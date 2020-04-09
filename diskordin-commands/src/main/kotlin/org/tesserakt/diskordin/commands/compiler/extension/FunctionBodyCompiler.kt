package org.tesserakt.diskordin.commands.compiler.extension

import io.github.classgraph.MethodInfo
import org.tesserakt.diskordin.commands.compiler.PersistentCompilerExtension
import org.tesserakt.diskordin.commands.feature.FunctionBody

class FunctionBodyCompiler : PersistentCompilerExtension<FunctionBody>() {
    override fun compileFeature(function: MethodInfo, name: String): FunctionBody {
        val method = function.loadClassAndGetMethod()

        return FunctionBody { parent: Any, params: List<Any?> ->
            method.invoke(parent, *params.toTypedArray())
        }
    }
}
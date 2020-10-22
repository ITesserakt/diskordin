package org.tesserakt.diskordin.commands.compiler.extension

import io.github.classgraph.MethodInfo
import org.tesserakt.diskordin.commands.compiler.FunctionCompilerExtension
import org.tesserakt.diskordin.commands.feature.AliasesFeature

@Suppress("UNCHECKED_CAST")
class AliasesCompiler : FunctionCompilerExtension<AliasesFeature>() {
    override fun compileFeature(function: MethodInfo, name: String): AliasesFeature? {
        val aliases = function
            .getAnnotationInfo(ALIASES)
            ?.parameterValues
            ?.getValue("aliases") as? Array<out String>

        return aliases?.let {
            AliasesFeature(name, it.toList())
        }
    }
}
package org.tesserakt.diskordin.commands.compiler.extension

import io.github.classgraph.MethodInfo
import org.tesserakt.diskordin.commands.compiler.FunctionCompilerExtension
import org.tesserakt.diskordin.commands.feature.HiddenFeature

class HiddenCompiler : FunctionCompilerExtension<HiddenFeature>() {
    override fun compileFeature(function: MethodInfo, name: String): HiddenFeature? {
        val isHidden = function.hasAnnotation(HIDE)

        return if (isHidden) HiddenFeature()
        else null
    }
}
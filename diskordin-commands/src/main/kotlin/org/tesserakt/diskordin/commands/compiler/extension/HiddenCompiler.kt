package org.tesserakt.diskordin.commands.compiler.extension

import io.github.classgraph.MethodInfo
import org.tesserakt.diskordin.commands.compiler.FunctionCompilerExtension
import org.tesserakt.diskordin.commands.compiler.PREFIX
import org.tesserakt.diskordin.commands.feature.HiddenFeature

class HiddenCompiler : FunctionCompilerExtension<HiddenFeature>() {
    private val hide = "$PREFIX.Hide"

    override fun compileFeature(function: MethodInfo, name: String): HiddenFeature? {
        val isHidden = function.hasAnnotation(hide)

        return if (isHidden) HiddenFeature()
        else null
    }
}
package org.tesserakt.diskordin.commands.compiler.extension

import io.github.classgraph.MethodInfo
import org.tesserakt.diskordin.commands.compiler.CompilerExtension
import org.tesserakt.diskordin.commands.compiler.PREFIX
import org.tesserakt.diskordin.commands.feature.HiddenFeature

class HiddenCompiler : CompilerExtension<HiddenFeature>() {
    private val hide = "$PREFIX.Hide"

    override fun compileFeature(function: MethodInfo, name: String): HiddenFeature? {
        val isHidden = function.hasAnnotation(hide)

        return if (isHidden) HiddenFeature()
        else null
    }
}
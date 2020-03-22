package org.tesserakt.diskordin.commands.compiler

import io.github.classgraph.MethodInfo
import org.tesserakt.diskordin.commands.Feature

abstract class CompilerExtension<F : Feature<F>> {
    abstract fun compileFeature(function: MethodInfo, name: String): F
}

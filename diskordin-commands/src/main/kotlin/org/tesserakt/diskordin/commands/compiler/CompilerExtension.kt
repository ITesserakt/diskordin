package org.tesserakt.diskordin.commands.compiler

import io.github.classgraph.MethodInfo
import org.tesserakt.diskordin.commands.feature.Feature
import org.tesserakt.diskordin.commands.feature.PersistentFeature

abstract class CompilerExtension<F : Feature<F>> {
    abstract fun compileFeature(function: MethodInfo, name: String): F?
}

abstract class PersistentCompilerExtension<F : PersistentFeature<F>> : CompilerExtension<F>() {
    abstract override fun compileFeature(function: MethodInfo, name: String): F
}
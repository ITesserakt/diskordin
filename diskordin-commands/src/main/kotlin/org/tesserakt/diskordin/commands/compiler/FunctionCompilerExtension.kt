package org.tesserakt.diskordin.commands.compiler

import io.github.classgraph.ClassInfo
import io.github.classgraph.MethodInfo
import org.tesserakt.diskordin.commands.feature.Feature
import org.tesserakt.diskordin.commands.feature.ModuleFeature
import org.tesserakt.diskordin.commands.feature.PersistentFeature

abstract class CompilerExtension<F : Feature<F>>

abstract class ModuleCompilerExtension<F : ModuleFeature<F>> : CompilerExtension<F>() {
    abstract fun compileModule(module: ClassInfo): F
}

abstract class FunctionCompilerExtension<F : Feature<F>> : CompilerExtension<F>() {
    abstract fun compileFeature(function: MethodInfo, name: String): F?
}

abstract class PersistentCompilerExtension<F : PersistentFeature<F>> : FunctionCompilerExtension<F>() {
    abstract override fun compileFeature(function: MethodInfo, name: String): F
}
package org.tesserakt.diskordin.commands.integration

import io.github.classgraph.ClassGraph
import org.tesserakt.diskordin.commands.compiler.CompilerExtension
import org.tesserakt.diskordin.commands.feature.Feature
import org.tesserakt.diskordin.commands.resolver.ResolversProvider

data class CommandFramework(
    val logger: Logger,
    val extraExtensions: Set<CompilerExtension<out Feature<*>>>,
    val eager: Boolean,
    val resolversProvider: ResolversProvider,
    val graph: ClassGraph
)
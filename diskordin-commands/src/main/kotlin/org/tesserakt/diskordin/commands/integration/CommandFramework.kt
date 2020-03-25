package org.tesserakt.diskordin.commands.integration

import org.tesserakt.diskordin.commands.compiler.CompilerExtension
import org.tesserakt.diskordin.commands.feature.Feature

data class CommandFramework(
    val logger: Logger,
    val extraExtensions: Set<CompilerExtension<out Feature<*>>>
)
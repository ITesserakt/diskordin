package org.tesserakt.diskordin.commands.compiler.extension

import io.github.classgraph.MethodInfo
import mu.KotlinLogging
import org.tesserakt.diskordin.commands.compiler.CompilerExtension
import org.tesserakt.diskordin.commands.compiler.PREFIX
import org.tesserakt.diskordin.commands.feature.Description

class DescriptionCompiler : CompilerExtension<Description>() {
    private val description = "$PREFIX.Description"
    private val logger = KotlinLogging.logger { }

    override fun compileFeature(function: MethodInfo, name: String): Description? {
        val value = function.getAnnotationInfo(description)
            ?.parameterValues
            ?.getValue("description") as? String

        return if (value.isNullOrBlank()) {
            logger.info("$name doesn't have description. Consider to add it")
            null
        } else Description(name, value)
    }
}
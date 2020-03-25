package org.tesserakt.diskordin.commands.compiler.extension

import io.github.classgraph.MethodInfo
import org.tesserakt.diskordin.commands.compiler.CompilerExtension
import org.tesserakt.diskordin.commands.compiler.PREFIX
import org.tesserakt.diskordin.commands.feature.DescriptionFeature
import org.tesserakt.diskordin.commands.integration.logger

class DescriptionCompiler : CompilerExtension<DescriptionFeature>() {
    private val description = "$PREFIX.Description"

    override fun compileFeature(function: MethodInfo, name: String): DescriptionFeature? {
        val value = function.getAnnotationInfo(description)
            ?.parameterValues
            ?.getValue("description") as? String

        return if (value.isNullOrBlank()) {
            logger.log("$name doesn't have description. Consider to add it")
            null
        } else DescriptionFeature(name, value)
    }
}
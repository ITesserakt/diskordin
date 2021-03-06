package org.tesserakt.diskordin.commands.compiler.extension

import io.github.classgraph.MethodInfo
import org.tesserakt.diskordin.commands.compiler.FunctionCompilerExtension
import org.tesserakt.diskordin.commands.feature.DescriptionFeature
import org.tesserakt.diskordin.commands.integration.logger

class DescriptionCompiler : FunctionCompilerExtension<DescriptionFeature>() {
    override fun compileFeature(function: MethodInfo, name: String): DescriptionFeature? {
        val value = function.getAnnotationInfo(DESCRIPTION)
            ?.parameterValues
            ?.getValue("description") as? String

        return if (value.isNullOrBlank()) {
            logger.log("$name doesn't have description. Consider to add it")
            null
        } else DescriptionFeature(name, value)
    }
}
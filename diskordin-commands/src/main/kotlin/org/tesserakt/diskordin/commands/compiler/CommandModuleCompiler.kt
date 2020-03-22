package org.tesserakt.diskordin.commands.compiler

import io.github.classgraph.AnnotationParameterValueList
import io.github.classgraph.ClassInfo
import io.github.classgraph.MethodInfo
import org.tesserakt.diskordin.commands.CommandBuilder

private const val PREFIX = "org.tesserakt.diskordin.commands"
private const val COMMAND = "$PREFIX.Command"
private const val DESCRIPTION = "$PREFIX.Description"
private const val ALIASES = "$PREFIX.Aliases"
private const val HIDE = "$PREFIX.Hide"

private inline fun <reified T : Any> AnnotationParameterValueList.getValueAs(parameterName: String) =
    getValue(parameterName) as? T

class CommandModuleCompiler(private val extensions: List<CompilerExtension<*>>) {
    fun compileModule(module: ClassInfo): List<CommandBuilder> {
        val commands = module.declaredMethodInfo
            .filter { it.hasAnnotation(COMMAND) }

        return commands.map(::compileFunction)
    }

    private fun compileFunction(function: MethodInfo): CommandBuilder {
        val commandName = function.getAnnotationInfo(COMMAND)
            .parameterValues
            .getValueAs<String>("name")
            ?.takeIf { it.isNotBlank() }

        val description = function.getAnnotationInfo(DESCRIPTION)
            ?.parameterValues
            ?.getValueAs<String>("description")

        val aliases = function.getAnnotationInfo(ALIASES)
            ?.parameterValues
            ?.getValueAs<List<String>>("aliases")
            ?: emptyList()

        val isHidden = function.hasAnnotation(HIDE)

        val features = extensions.map { it.compileFeature(function) }.toSet()

        return CommandBuilder().apply {
            +name(commandName ?: function.name)
            if (description != null)
                +description(description)
            if (isHidden)
                +hide()
            +aliases
            +features
        }
    }
}
package org.tesserakt.diskordin.commands.compiler

import io.github.classgraph.AnnotationParameterValueList
import io.github.classgraph.ClassInfo
import io.github.classgraph.MethodInfo
import org.tesserakt.diskordin.commands.CommandBuilder

internal const val PREFIX = "org.tesserakt.diskordin.commands"
private const val COMMAND = "$PREFIX.Command"
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
            ?.takeIf { it.isNotBlank() } ?: function.name

        val aliases = function.getAnnotationInfo(ALIASES)
            ?.parameterValues
            ?.getValueAs<List<String>>("aliases")
            ?: emptyList()

        val isHidden = function.hasAnnotation(HIDE)

        val features = extensions.mapNotNull { it.compileFeature(function, commandName) }.toSet()

        return CommandBuilder().apply {
            +name(commandName)
            if (isHidden)
                +hide()
            +aliases
            +features
        }
    }
}
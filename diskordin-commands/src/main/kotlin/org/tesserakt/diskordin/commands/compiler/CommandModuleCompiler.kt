package org.tesserakt.diskordin.commands.compiler

import io.github.classgraph.AnnotationParameterValueList
import io.github.classgraph.ClassInfo
import io.github.classgraph.MethodInfo
import org.tesserakt.diskordin.commands.CommandBuilder

internal const val PREFIX = "org.tesserakt.diskordin.commands"
private const val COMMAND = "$PREFIX.Command"

private inline fun <reified T : Any> AnnotationParameterValueList.getValueAs(parameterName: String) =
    getValue(parameterName) as? T

class CommandModuleCompiler(private val extensions: Set<CompilerExtension<*>>) {
    fun compileModule(module: ClassInfo): List<CommandBuilder> {
        val extensions = extensions.filterIsInstance<ModuleCompilerExtension<*>>()
        val commands = module.declaredMethodInfo
            .filter { it.hasAnnotation(COMMAND) }

        val features = extensions.map { it.compileModule(module) }.toSet()

        return commands.map(::compileFunction).map {
            it.apply { +features }
        }
    }

    private fun compileFunction(function: MethodInfo): CommandBuilder {
        val commandName = function.getAnnotationInfo(COMMAND)
            .parameterValues
            .getValueAs<String>("name")
            ?.takeIf { it.isNotBlank() } ?: function.name

        val features = extensions
            .filterIsInstance<FunctionCompilerExtension<*>>()
            .mapNotNull { it.compileFeature(function, commandName) }.toSet()

        return CommandBuilder().apply {
            +name(commandName)
            +features
        }
    }
}
package org.tesserakt.diskordin.commands.compiler

import io.github.classgraph.ClassInfo
import io.github.classgraph.ClassRefTypeSignature
import io.github.classgraph.MethodInfo
import org.tesserakt.diskordin.commands.feature.Feature
import org.tesserakt.diskordin.commands.feature.ModuleFeature
import org.tesserakt.diskordin.commands.feature.PersistentFeature
import java.lang.reflect.Method

abstract class CompilerExtension<F : Feature<F>> {
    protected fun ClassRefTypeSignature.subtypeOf(className: String) =
        if (this.classInfo == null) this.loadClass().interfaces.contains(Class.forName(className))
        else classInfo.implementsInterface(className) or classInfo.extendsSuperclass(className)

    companion object Constants {
        const val PREFIX = "org.tesserakt.diskordin.commands"
        const val COMMAND_CONTEXT = "$PREFIX.CommandContext"
        const val COMMAND = "$PREFIX.Command"
        const val IGNORE = "$PREFIX.Ignore"
        const val ALIASES = "$PREFIX.Aliases"
        const val DESCRIPTION = "$PREFIX.Description"
        const val HIDE = "$PREFIX.Hide"
    }

    data class NotKotlinFunction(val function: Method) :
        Throwable("${function.name} cannot be represented as a Kotlin function")
}

abstract class ModuleCompilerExtension<F : ModuleFeature<F>> : CompilerExtension<F>() {
    abstract fun compileModule(module: ClassInfo): F
}

abstract class FunctionCompilerExtension<F : Feature<F>> : CompilerExtension<F>() {
    abstract fun compileFeature(function: MethodInfo, name: String): F?
}

abstract class PersistentCompilerExtension<F : PersistentFeature<F>> : FunctionCompilerExtension<F>() {
    abstract override fun compileFeature(function: MethodInfo, name: String): F
}
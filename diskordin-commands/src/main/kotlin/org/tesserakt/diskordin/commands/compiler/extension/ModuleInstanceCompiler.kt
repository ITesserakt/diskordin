package org.tesserakt.diskordin.commands.compiler.extension

import io.github.classgraph.ClassInfo
import org.tesserakt.diskordin.commands.CommandModule
import org.tesserakt.diskordin.commands.compiler.ModuleCompilerExtension
import org.tesserakt.diskordin.commands.feature.ModuleInstanceFeature
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubclassOf

class ModuleInstanceCompiler : ModuleCompilerExtension<ModuleInstanceFeature>() {
    @Suppress("UNCHECKED_CAST")
    override fun compileModule(module: ClassInfo): ModuleInstanceFeature {
        val moduleType = module.loadClass(CommandModule::class.java).kotlin

        fun KClass<CommandModule<*>>.getInstance(): CommandModule<*> {
            val zeroArgCtor = if (constructors.singleOrNull { it.parameters.all(KParameter::isOptional) } != null)
                createInstance()
            else null

            val companionObj = nestedClasses
                .find { it.isSubclassOf(CommandModule.Factory::class) }
                ?.let { it as? KClass<out CommandModule.Factory> }?.objectInstance

            return zeroArgCtor
                ?: companionObj?.create()
                ?: throw IllegalStateException("Could not find any viable creator for ${qualifiedName ?: "<local class>"}")
        }

        return ModuleInstanceFeature(
            moduleType,
            moduleType.getInstance()
        )
    }
}
package org.tesserakt.diskordin.commands.compiler.extension

import io.github.classgraph.ClassInfo
import org.tesserakt.diskordin.commands.CommandModule
import org.tesserakt.diskordin.commands.compiler.ModuleCompilerExtension
import org.tesserakt.diskordin.commands.feature.ModuleInstanceFeature
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

class ModuleInstanceCompiler : ModuleCompilerExtension<ModuleInstanceFeature>() {
    @Suppress("UNCHECKED_CAST")
    override fun compileModule(module: ClassInfo): ModuleInstanceFeature {
        val moduleType = module.loadClass(CommandModule::class.java)

        fun getInstance(type: Class<CommandModule<*, *>>): CommandModule<*, *> {
            val zeroArgCtor = type.constructors.find { it.parameterCount == 0 }
            val companionObj = type.kotlin.nestedClasses
                .find { it.isSubclassOf(CommandModule.Factory::class) }
                ?.let { it as? KClass<out CommandModule.Factory> }?.objectInstance

            return zeroArgCtor?.newInstance()?.let { it as CommandModule<*, *> }
                ?: companionObj?.create()
                ?: throw IllegalStateException("Could not find any viable creator for ${type.name}")
        }

        return ModuleInstanceFeature(
            moduleType,
            getInstance(moduleType)
        )
    }
}
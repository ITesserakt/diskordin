package org.tesserakt.diskordin.commands.compiler.extension

import io.github.classgraph.MethodInfo
import org.tesserakt.diskordin.commands.CommandModule
import org.tesserakt.diskordin.commands.compiler.PersistentCompilerExtension
import org.tesserakt.diskordin.commands.feature.ModuleFeature
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

class ModuleCompiler : PersistentCompilerExtension<ModuleFeature>() {
    private val precompiled = mutableMapOf<Class<CommandModule<*, *>>, CommandModule<*, *>>()

    @Suppress("UNCHECKED_CAST")
    override fun compileFeature(function: MethodInfo, name: String): ModuleFeature {
        val moduleType = function.classInfo.loadClass(CommandModule::class.java)

        fun getInstance(type: Class<CommandModule<*, *>>): CommandModule<*, *> {
            val zeroArgCtor = type.constructors.find { it.parameterCount == 0 }
            val companionObj = type.kotlin.nestedClasses
                .find { it.isSubclassOf(CommandModule.Factory::class) }
                ?.let { it as? KClass<out CommandModule.Factory> }?.objectInstance

            return (zeroArgCtor?.newInstance()?.let { it as CommandModule<*, *> }
                ?: companionObj?.create()
                ?: throw IllegalStateException("Could not find any viable creator for ${type.name}")).also {
                precompiled += moduleType to it
            }
        }

        return ModuleFeature(
            moduleType,
            precompiled[moduleType] ?: getInstance(moduleType)
        )
    }
}